package com.willjo.controller;

import com.willjo.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {

    @Resource
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/trans/test")
    public Boolean transTest() {
        return userService.transMessageSuccess();
    }
}