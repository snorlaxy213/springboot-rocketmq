package com.willjo.service.impl;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.willjo.dal.entity.UserEntity;
import com.willjo.dal.mapper.UserMapper;
import com.willjo.exception.SaveUserException;
import com.willjo.mq.constant.MqConstant;
import com.willjo.service.MqTransMessageService;
import com.willjo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final SecureRandom secureRandom = new SecureRandom();
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
        UserEntity userEntity = saveUser();
        
        //发送消息
        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_ORDER_TAG, JSONUtil.toJsonStr(userEntity));
        return Boolean.TRUE;
    }
    
    private UserEntity saveUser() {
        UserEntity userEntity = new UserEntity();
        // 使用更安全的随机生成方法
        userEntity.setUsername(generateSecureUsername());
        // 随机生成userEntity的Age
        userEntity.setAge(secureRandom.nextInt(100) + 1);
        // 保存
        try {
            Boolean saved = this.save(userEntity);
            if (saved) {
                return userEntity;
            } else {
                throw new SaveUserException("Failed to save user");
            }
        } catch (Exception e) {
            // 记录异常日志
            LOGGER.error("Failed to save user: ", e);
            throw new SaveUserException("Failed to save user", e);
        }
    }
    
    /**
     * 返回随机生成的用户名
     * @return 随机生成的用户名
     */
    private String generateSecureUsername() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(allowedChars.charAt(secureRandom.nextInt(allowedChars.length())));
        }
        return sb.toString();
    }
    
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transMessageError() throws Exception {
        //保存用户信息
        saveUser();

        //发送消息
        LOGGER.info("begin transMessageError");
        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_ORDER_TAG,
                "{\"userName\": \"WillJoError\"}");
        TimeUnit.SECONDS.sleep(10);
        mqTransMessageService.transSendMsg(MqConstant.Top.USER_ORDER_TOPIC, MqConstant.Tag.USER_ORDER_TAG, "{\"userName\": \"WillJoError\"}");
        LOGGER.info(" end transMessageError");
        throw new RuntimeException();
    }
}