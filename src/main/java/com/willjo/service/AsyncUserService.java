package com.willjo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.willjo.dal.entity.UserEntity;

import java.util.List;
import java.util.concurrent.Semaphore;

public interface AsyncUserService extends IService<UserEntity> {
    
    void saveVirtualUser();
    
    void saveUser(List<UserEntity> userEntities);
    
    void updateAge(Semaphore semaphore);
}