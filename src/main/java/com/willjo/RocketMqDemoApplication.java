package com.willjo;

import com.willjo.annotation.EnableRocketMq;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRocketMq
@SpringBootApplication(scanBasePackages = "com.willjo")
@MapperScan("com.willjo.dal.mapper")
@EnableScheduling
@EnableAsync
public class RocketMqDemoApplication {

    /**
     * 程序的入口点
     * 这个方法启动Spring Boot应用程序，并在控制台打印启动成功的消息
     *
     * @param args 命令行参数，用于传递给Spring Boot应用程序
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用程序
        SpringApplication.run(RocketMqDemoApplication.class, args);
    }

}