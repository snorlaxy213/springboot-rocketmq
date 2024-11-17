package com.willjo.controller;

import com.willjo.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class HelloController {

    @Resource
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/save10000user")
    public Boolean save10000user() {
        return userService.save10000user();
    }
    
    @GetMapping("/update10Age")
    public Boolean update10Age() {
        return userService.update10Age();
    }
}