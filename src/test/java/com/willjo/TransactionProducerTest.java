package com.willjo;

import com.willjo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = RocketMqDemoApplication.class)
@RunWith(SpringRunner.class)
public class TransactionProducerTest {

    @Autowired
    private UserService userService;

    /**
     * 测试本地事务成功的时候发送消息也成功
     */
    @Test
    @Rollback(value = true)
    public void testSuccess() {
        //记录开始时间
        long start = System.currentTimeMillis();
        //测试1000次
        for (int i = 0; i < 1000; i++) {
            userService.transMessageSuccess();
        }
        //记录结束时间
        long end = System.currentTimeMillis();
        //计算程序执行时间
        System.out.println("程序执行时间: " + (end - start) + "ms");
    }

    /**
     * 测试本地事务失败的时候消息也发送失败
     */
    @Test
    @Rollback(value = false)
    public void testError() throws Exception {
        userService.transMessageError();
    }
}
