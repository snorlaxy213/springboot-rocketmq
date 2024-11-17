package com.willjo.service;

import com.baomidou.mybatisplus.service.IService;
import com.willjo.dal.entity.UserEntity;

import java.util.concurrent.Semaphore;

public interface AsyncUserService extends IService<UserEntity> {
    
    void saveUser();
    
    void updateAge(Semaphore semaphore);
}