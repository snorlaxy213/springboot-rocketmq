package com.willjo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.willjo.dal.entity.UserEntity;

import java.util.List;
import java.util.concurrent.Semaphore;

public interface AsyncUserService extends IService<UserEntity> {

    /**
     * 保存虚拟用户
     */
    void saveVirtualUser();

    /**
     * 批量保存用户
     *
     * @param userEntities 用户
     */
    void batchSaveUser(List<UserEntity> userEntities);

    /**
     * 10个线程同时调用获取Age，自增加一
     *
     * @param semaphore 信号量
     */
    void updateAge(Semaphore semaphore);
}