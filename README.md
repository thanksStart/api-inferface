# SpringBoot+Dubbo 项目

一个基于React + Springboot + Dubbo + Gateway 实现API接口开发平台。


## 功能

1.使用了Spring Boot Starter 开发了客户端的SDK，解决了开发者在调用时开发成本过高的问题（需要自己使用HTTP + 封装签名的方式去调用接口），增加了开发者的开发体验。

2.选用了Spring Cloud Gateway作为API网关，集中处理了用户签名校验、请求参数校验和接口调用次数统计等业务逻辑，并且实现了路由转发和流量染色等。

3.使用了Dubbo RPC框架和Nacos注册中心，实现了Web子项目和Gateway子项目之间一些方法接口之间的高效调用，解决了各个子项之间大量代码需要重复编写的问题。

访问 localhost:8080/api/doc.html 就能在线调试接口了，不需要前端配合啦~