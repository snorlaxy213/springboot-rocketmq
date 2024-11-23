package com.willjo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SpringBootConfig {

    @Bean("UserImportExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 核心线程数
        executor.setMaxPoolSize(20); // 最大线程数
        executor.setQueueCapacity(200); // 队列大小
        executor.setKeepAliveSeconds(30); // 线程空闲时间
        executor.setAllowCoreThreadTimeOut(true); // 是否允许核心线程超时
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略（由调用线程处理新任务）
        executor.setThreadNamePrefix("UserImportExecutor-"); // 线程前缀
        executor.initialize();
        return executor;
    }
}