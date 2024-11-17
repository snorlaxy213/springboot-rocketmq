package com.willjo.service;

import com.baomidou.mybatisplus.service.IService;
import com.willjo.dal.entity.UserEntity;

public interface AsyncUserService extends IService<UserEntity> {
    
    void saveUser();
}