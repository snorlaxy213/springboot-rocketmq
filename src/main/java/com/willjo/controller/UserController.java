package com.willjo.controller;

import com.alibaba.excel.EasyExcel;
import com.willjo.dal.entity.UserEntity;
import com.willjo.easyexcel.UserListener;
import com.willjo.service.AsyncUserService;
import com.willjo.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Resource
    @Qualifier("asyncUserServiceImpl")
    private AsyncUserService asyncUserService;

    @GetMapping("/save10000user")
    public Boolean save10000user() {
        return userService.save10000user();
    }

    @GetMapping("/update10Age")
    public void update10Age() {
        userService.update10Age();
    }

    @PostMapping("/uploadUserExcel")
    public Boolean uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        //通过EasyExcel把file中的数据解析出来
        EasyExcel.read(file.getInputStream(), UserEntity.class, new UserListener(asyncUserService)).sheet().doRead();
        return Boolean.TRUE;
    }

}