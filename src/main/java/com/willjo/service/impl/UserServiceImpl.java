package com.willjo.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.willjo.dal.entity.UserEntity;
import com.willjo.dal.mapper.UserMapper;
import com.willjo.mq.constant.MqConstant;
import com.willjo.service.MqTransMessageService;
import com.willjo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Grio Vino
 * @since 2019-02-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @Resource
    @Qualifier(value = "userMapper")
    private UserMapper userMapper;

    @Resource
    @Qualifier(value = "mqTransMessageServiceImpl")
    private MqTransMessageService mqTransMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(UserEntity userEntity) {
        return super.insert(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transMessageSuccess() {
        //保存用户信息
        saveUser();

        //发送消息
        LOGGER.info("begin transMessageSuccess");
        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_TAG, "{\"userName\": \"WillJoSuccess\"}");
        LOGGER.info("end transMessageSuccess");
        return Boolean.TRUE;
    }

    private void saveUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setAge(11);
        userEntity.setUsername("trans");
        this.save(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transMessageError() throws Exception {
        //保存用户信息
        saveUser();

        //发送消息
        LOGGER.info("begin transMessageError");
        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_TAG, "{\"userName\": \"WillJoError\"}");
        LOGGER.info(" end transMessageError");
        throw new RuntimeException();
    }
}