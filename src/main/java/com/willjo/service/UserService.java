package com.willjo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.willjo.dal.entity.UserEntity;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Grio Vino
 * @since 2019-02-23
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 发送事务消息成功
     *
     * @return true/false
     */
    Boolean save10000user();

    /**
     * 10个线程同时调用获取Age，自增加一
     */
    void update10Age();

}