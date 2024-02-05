package com.start.apigateway;

import com.start.apiclientsdk.util.SignUtil;
import com.start.apicommon.model.entity.InterfaceInfo;
import com.start.apicommon.model.entity.User;
import com.start.apicommon.model.entity.UserInterfaceInfo;
import com.start.apicommon.service.InnerUserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局网关过滤
 *
 * @author start
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    public static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    public static final String INTERFACE_HOST = "http://localhost:8520";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//      1.用户发送请求到API网关(请求到这一步已经完成)
//      2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        //拿到响应头
        ServerHttpResponse response = exchange.getResponse();
//      3.(黑白名单)
        if(!IP_WHITE_LIST.contains(sourceAddress)){
            return handleNoAuth(response);
        }
//      4.用户鉴权（ak，sk）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String body = headers.getFirst("body");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // 需要到数据中查询这个是否正确
        User invokeUser = null;
        try {
            invokeUser = innerUserInterfaceInfoService.getInvokeUserKey(accessKey);
        }catch (Exception e){
            log.error("getInvokeUserKey error" + e);
        }
        if(invokeUser == null){
            return handleNoAuth(response);
        }
        // todo 随机数需要存储在数据库中，然后去数据库中判断这个随机数是否符合规定
        if(Long.parseLong(nonce) > 10000){
            return handleNoAuth(response);
        }
        //时间戳也是需要校验的
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if((currentTime - Long.parseLong(timestamp)) > FIVE_MINUTES){
            return handleNoAuth(response);
        }
        //实际情况是从数据库中查询到对应用户的secretKey的值
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.getSign(body, secretKey);
        if(sign == null || !sign.equals(serverSign)){
            throw new RuntimeException("无权限");
        }
//      5.判断请求的模拟接口是否合法存在
        //从数据库中查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerUserInterfaceInfoService.getInterfaceInfo(path, method);
        }catch (Exception e){
            log.error("getInterfaceInfo error" + e);
        }
        if(interfaceInfo == null){
            return handleNoAuth(response);
        }
        //判断剩余次数是否足够
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = invokeUser.getId();
        UserInterfaceInfo userInterfaceInfo = null;
        try {
            userInterfaceInfo = innerUserInterfaceInfoService.getLeftNumber(interfaceInfoId, userId);
        }catch (Exception e){
            log.error("getLeftNumber error" + e);
        }
        if(userInterfaceInfo == null){
            return handleNoAuth(response);
        }
        Integer leftNumber = userInterfaceInfo.getLeftNumber();
        if(leftNumber <= 0){
            return handleNoAuth(response);
        }
//      6.请求转发，调用模拟接口
//      Mono<Void> filter = chain.filter(exchange);
//      return filter;
        return handleResponse(exchange,chain, interfaceInfoId, userId);

    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceId, long userId){
        try {
            //拿到老的响应头
            ServerHttpResponse originalResponse = exchange.getResponse();
            //创建数据缓存工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应头的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if(statusCode == HttpStatus.OK){
                //拿到装饰响应头,如果不用装饰者的话，网关会走完过滤器才会再去调用接口
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //等调用完成接口之后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值中写数据
                            //拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //   todo   8.调用成功，接口调用次数 + 1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceId, userId);
                                }catch (Exception e){
                                    log.error("invokeCount error" + e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //      7.响应日志
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            //  todo    9.调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("响应异常处理" + e);
            return chain.filter(exchange);
        }
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        //添加响应状态码，将其拦截
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response){
        //添加响应状态码，将其拦截
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}
