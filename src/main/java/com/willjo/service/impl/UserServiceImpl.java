package com.willjo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willjo.dal.entity.UserEntity;
import com.willjo.dal.mapper.UserMapper;
import com.willjo.service.AsyncUserService;
import com.willjo.service.MqTransMessageService;
import com.willjo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Semaphore;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    @Qualifier(value = "userMapper")
    private UserMapper userMapper;

    @Resource
    @Qualifier(value = "mqTransMessageServiceImpl")
    private MqTransMessageService mqTransMessageService;

    @Resource
    @Qualifier(value = "asyncUserServiceImpl")
    private AsyncUserService asyncUserService;

    @Override
    public Boolean save10000user() {
        for (int i = 0; i < 50; i++) {
            //保存用户信息
            asyncUserService.saveVirtualUser();

            //发送消息
//        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_ORDER_TAG, JSONUtil.toJsonStr(userEntity));
        }
        return Boolean.TRUE;
    }

    /**
     * 10个线程同时调用获取Age，自增加一
     */
    @Override
    public void update10Age() {
        Semaphore semaphore = new Semaphore(1);
        for (int i = 0; i < 10; i++) {
            asyncUserService.updateAge(semaphore);
        }
    }
}