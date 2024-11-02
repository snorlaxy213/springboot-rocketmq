package com.willjo.controller;

import com.willjo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Grio Vino
 * @since 2024-09-26
 **/
@RestController
public class HelloController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/trans/test")
    public Boolean transTest() throws Exception {
        return userService.transMessageSuccess();

    }
}
