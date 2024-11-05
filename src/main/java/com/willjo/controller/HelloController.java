package com.willjo.controller;

import com.willjo.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author Grio Vino
 * @since 2024-09-26
 **/
@RestController
public class HelloController {

    @Resource
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/trans/test")
    public Boolean transTest() throws Exception {
        //记录开始时间
        long start = System.currentTimeMillis();
        //测试1000次
        for (int i = 0; i < 1000; i++) {
            userService.transMessageSuccess();
        }
        //记录结束时间
        long end = System.currentTimeMillis();
        //计算程序执行时间
        System.out.println("程序执行时间: " + (end - start) + "ms");
        return Boolean.TRUE;
    }

    @GetMapping("/trans/error")
    public Boolean transError() throws Exception {
        return userService.transMessageError();
    }
}