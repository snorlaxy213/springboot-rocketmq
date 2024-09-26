package com.willjo.mq;

import com.willjo.enums.MqAction;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;


/**
 * 普通消息监听
 *
 * @author Grio Vino
 * @since 2024-09-26
 **/
public interface MessageListener {

    /**
     * mq 消费接口
     */
    MqAction consume(MessageExt var1, ConsumeConcurrentlyContext context);
}
