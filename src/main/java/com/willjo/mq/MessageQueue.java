package com.willjo.mq;

import com.willjo.dal.entity.MqTransMessageEntity;
import com.willjo.mq.message.MqTransMessage;
import com.willjo.mq.message.MqTransMessageDelay;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 添加消息进入队列
 *
 * @author willJo
 * @since 2024-09-26
 */
public class MessageQueue {

    /**
     * 优先级最高的
     */
    public static BlockingQueue<MqTransMessage> priorityQueue = new LinkedBlockingDeque<>();

    /**
     * 延迟队列
     */
    public static DelayQueue<MqTransMessageDelay> delayQueue = new DelayQueue<>();

    public static boolean putInPriorityQueue(MqTransMessageEntity mqTransMessageEntity) {
        return priorityQueue.add(MqTransMessage.instance(mqTransMessageEntity));
    }

    public static boolean putInDelayQueue(MqTransMessage transMessage) {
        transMessage.setFailCount(transMessage.getFailCount() + 1);
        return delayQueue.add(MqTransMessageDelay.instance(transMessage));
    }


}