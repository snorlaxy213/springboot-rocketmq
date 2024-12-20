package com.willjo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willjo.dal.entity.UserEntity;
import com.willjo.dal.mapper.UserMapper;
import com.willjo.exception.SaveUserException;
import com.willjo.service.AsyncUserService;
import com.willjo.util.GeneratorId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service("asyncUserServiceImpl")
public class AsyncUserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements AsyncUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncUserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存虚拟用户
     */
    @Override
    @Async("UserImportExecutor")
    public void saveVirtualUser() {
        //记录开始时间
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            UserEntity userEntity = new UserEntity();
            // 使用更安全的随机生成方法
            userEntity.setUsername(GeneratorId.generateSecureUsername());
            // 随机生成userEntity的Age
            userEntity.setAge(new SecureRandom().nextInt(100) + 1);
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
        LOGGER.info("方法名：保存虚拟用户，线程名：{}，程序执行时间: {}ms", Thread.currentThread().getName(), end - start);
    }

    /**
     * 批量保存用户
     *
     * @param users 用户
     */
    @Override
    @Async("UserImportExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUser(List<UserEntity> users) {
        //记录开始时间
        long start = System.currentTimeMillis();
        userMapper.userBatchInsert2(users);
        //记录结束时间
        long end = System.currentTimeMillis();
        LOGGER.info("方法名：批量保存用户，线程名：{}，程序执行时间: {}ms", Thread.currentThread().getName(), end - start);
    }

    /**
     * 10个线程同时调用获取Age，自增加一
     *
     * @param semaphore 信号量
     */
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
}