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
        return userService.transMessageSuccess();
    }

    @GetMapping("/trans/error")
    public Boolean transError() throws Exception {
        return userService.transMessageError();
    }
}