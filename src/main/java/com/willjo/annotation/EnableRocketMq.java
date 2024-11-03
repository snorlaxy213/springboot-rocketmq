package com.willjo.annotation;

import com.willjo.config.RocketMqFactoryBeanConfig;
import com.willjo.config.RocketMqProperties;
import com.willjo.mq.*;
import com.willjo.mq.readyevent.MonitorQueueReadyEvent;
import com.willjo.mq.readyevent.TransDelayMessageReadyEvent;
import com.willjo.mq.readyevent.TransMessageReadyEvent;
import com.willjo.mq.runner.RocketMqConsumerRunner;
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
        RocketMqConsumerRunner.class,
        RocketMqProducerUtil.class,
        RocketMqProperties.class,
        RocketMqFactoryBeanConfig.class,
        TransMessageReadyEvent.class,
        TransDelayMessageReadyEvent.class,
        MonitorQueueReadyEvent.class
})
public @interface EnableRocketMq {

}