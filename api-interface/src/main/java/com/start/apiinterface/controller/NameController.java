package com.start.apiinterface.controller;

import com.start.apiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 *
 * @author start
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String nameByGet(String name){
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String nameByPost(@RequestParam String name){
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String nameUserByPost(@RequestBody User user, HttpServletRequest request){
        return "POST 用户名字是" + user.getUsername();
    }
}
