package com.willjo.service;

import com.baomidou.mybatisplus.service.IService;
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
     * 新增用户
     */
    Boolean save(UserEntity userEntity);

    /**
     * 发送事务消息成功
     *
     * @return true/false
     */
    Boolean transMessageSuccess();
    
}