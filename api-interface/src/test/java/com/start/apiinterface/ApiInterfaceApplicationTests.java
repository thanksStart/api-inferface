package com.start.apiinterface;

import com.start.apiclientsdk.client.ApiClient;
import com.start.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {
    @Resource
    private ApiClient apiClient;

    @Test
    void contextLoads() {
        String result = apiClient.nameByGet("start");
        User user = new User();
        user.setUsername("start");
        String result1 = apiClient.nameUserByPost(user);
        System.out.println(result + result1 + "...........");
    }

}
