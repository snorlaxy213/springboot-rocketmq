package com.willjo.mq.readyevent;

import com.willjo.dal.entity.MqTransMessageEntity;
import com.willjo.mq.message.MqTransMessage;
import com.willjo.mq.constant.MessageLockConstant;
import com.willjo.mq.MessageQueue;
import com.willjo.mq.RocketMqProducerUtil;
import com.willjo.service.MqTransMessageService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Grio Vino
 * @since 2024-09-26
 */
public class TransMessageReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TransMessageReadyEvent.class);

    /**
     * 事务最大等待时间，单位为秒
     */
    public static final int TRANS_MAX_WAITING_TIME = 30;

    @Autowired
    private RocketMqProducerUtil rocketMqProducerUtil;

    @Autowired
    private MqTransMessageService mqTransMessageService;

    @Override
public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    // 当应用程序准备就绪时，启动消息发送线程
    System.out.println("run message send thread");
    new Thread(() -> {
        // 持续监听并处理优先级消息队列
        while (true) {
            MqTransMessage message = null;
            try {
                // 从优先级队列中获取消息
                message = MessageQueue.priorityQueue.take();
            } catch (InterruptedException e) {
                // 捕获中断异常，以便在发生中断时适当处理
            }
            if (Objects.isNull(message)) {
                // 如果消息为空，则继续下一次循环
                continue;
            }
            try {
                SendResult sendResult = null;
                // 生成消息锁的键
                String key = MessageFormat.format(MessageLockConstant.LOCK_PREFIX, message.getId());
                synchronized (key.intern()) {
                    // 查询数据库确保消息存在
                    MqTransMessageEntity mqTransMessageEntity = mqTransMessageService.selectById(message.getId());
                    if (Objects.isNull(mqTransMessageEntity)) {
                        // 如果数据库中没有此消息，判断可能的情况
                        long time = System.currentTimeMillis() - message.getCreateTime().getTime();
                        if (time / 1000 > TRANS_MAX_WAITING_TIME) {
                            // 如果超过最大等待时间（例如30秒），则丢弃消息
                            logger.info(" due to over 30 second, discard message for messageId={}", message.getId());
                        } else {
                            // 否则，将消息放入延迟队列处理
                            logger.info(" add message to delayQueue  for messageId={}", message.getId());
                            MessageQueue.putInDelayQueue(message);
                        }
                        continue;
                    } else {
                        // 使用RocketMQ生产者同步发送消息
                        sendResult = rocketMqProducerUtil.synSend(message.getTopic(), message.getTag(), "", message.getMessage());
                        if (Objects.nonNull(sendResult) && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                            // 如果发送成功，从数据库中删除该消息
                            mqTransMessageService.deleteById(message.getId());
                        } else {
                            // 如果发送失败（例如网络问题），将消息重新放入优先级队列进行发送
                            MessageQueue.priorityQueue.put(message);
                        }
                    }
                }
            } catch (Exception e) {
                // 捕获异常，记录日志，并将消息放入延迟队列处理
                logger.warn("mq send fail,message={}", e.getMessage(), e);
                MessageQueue.putInDelayQueue(message);
            }
        }
    }, "transMessage").start();
}

}