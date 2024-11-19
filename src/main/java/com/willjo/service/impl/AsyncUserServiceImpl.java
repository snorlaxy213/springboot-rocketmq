package com.willjo.service.impl;


import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willjo.dal.entity.UserEntity;
import com.willjo.dal.mapper.UserMapper;
import com.willjo.exception.SaveUserException;
import com.willjo.service.AsyncUserService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service("asyncUserServiceImpl")
public class AsyncUserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements AsyncUserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncUserServiceImpl.class);

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Override
    @Async("UserImportExecutor")
    public void saveVirtualUser() {
        //记录开始时间
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            UserEntity userEntity = new UserEntity();
            // 使用更安全的随机生成方法
            userEntity.setUsername(generateSecureUsername());
            // 随机生成userEntity的Age
            userEntity.setAge(secureRandom.nextInt(100) + 1);
            // 保存
            try {
                boolean saved = super.save(userEntity);
                if (!saved) {
                    throw new SaveUserException("Failed to save user");
                }
            } catch (Exception e) {
                // 记录异常日志
                LOGGER.error("Failed to save user: ", e);
                throw new SaveUserException("Failed to save user", e);
            }
        }
        //记录结束时间
        long end = System.currentTimeMillis();
        LOGGER.info("线程名：{}，程序执行时间: {}ms", Thread.currentThread().getName(), end - start);
    }
    
    @Override
    @Async("UserImportExecutor")
    public void saveUser(List<UserEntity> userEntities) {
        //记录开始时间
        long start = System.currentTimeMillis();
        MybatisBatch<UserEntity> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, userEntities);
        MybatisBatch.Method<UserEntity> method = new MybatisBatch.Method<>(UserMapper.class);
        mybatisBatch.execute(method.get("userBatchInsert"));
        //记录结束时间
        long end = System.currentTimeMillis();
        LOGGER.info("线程名：{}，程序执行时间: {}ms", Thread.currentThread().getName(), end - start);
    }
    
    @Override
    @Async("UserImportExecutor")
    public void updateAge(Semaphore semaphore) {
        try {
            //获取信号量
            semaphore.acquire();
            
            //获取Age
            UserEntity userEntity = getById(96196);
            
            //更新Age
            userEntity.setAge(userEntity.getAge() + 1);
            updateById(userEntity);
            
            //释放信号量
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
}