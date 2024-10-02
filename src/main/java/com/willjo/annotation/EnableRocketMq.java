package com.willjo.annotation;

import com.willjo.config.RocketMqFactoryBeanConfig;
import com.willjo.config.RocketMqProperties;
import com.willjo.mq.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用mq 注解
 *
 * @author Grio Vino
 * @since 2020/3/17 15:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        RocketMqProperties.class,
        RocketMqConsumerRunner.class,
        RocketMqProducerService.class,
        RocketMqProperties.class,
        RocketMqFactoryBeanConfig.class,
        TransMessageRunner.class,
        TransDelayMessageRunner.class,
        MonitorQueue.class
})
public @interface EnableRocketMq {

}