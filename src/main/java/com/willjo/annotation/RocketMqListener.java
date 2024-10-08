package com.willjo.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 普通消息监听
 *
 * @author Grio Vino
 * @since 2019/1/4 下午10:11
 **/

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RocketMqListener {

    String value() default "";

    String topic();

    String tag() default "*";

    String consumerGroup();
}
