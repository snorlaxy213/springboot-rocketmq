package com.willjo;

import com.willjo.annotation.EnableRocketMq;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Grio Vino
 * @since 2019/2/16 17:46
 */
@EnableRocketMq
@SpringBootApplication(scanBasePackages = "com.willjo")
@MapperScan("com.willjo.dal.mapper")
@EnableScheduling
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
        // 打印启动成功的消息
        System.out.println("start success");
    
    }

}

