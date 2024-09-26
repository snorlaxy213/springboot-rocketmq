package com.willjo.mq;

import com.willjo.exception.MqContextException;
import com.willjo.exception.MqSendException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Grio Vino
 * @since 2019/2/16 17:46
 */
public class RocketMqProducerService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqProducerService.class);
    
    private final RocketSendCallback rocketSendCallback = new RocketSendCallback();
    @Resource
    @Qualifier("defaultProducer")
    private DefaultMQProducer rocketProducer;
    
    /**
     * 单边发送消息
     * 本方法负责将消息发送到指定的主题和标签下发送方式为单边发送，即发送消息后不等待响应
     * 主要用于对实时性要求不高，且不需知道发送结果的场景
     *
     * @param topic 消息主题
     * @param tag 消息标签，用于对消息进行分类
     * @param keys 消息的键，用于唯一标识消息
     * @param content 消息内容
     */
    public void sendOneway(String topic, String tag, String keys, String content) {
        try {
            // 创建Message对象
            Message msg = getMessage(topic, tag, keys, content);
            
            // 发送消息
            rocketProducer.sendOneway(msg);
            
            // 记录日志
            this.logMsg(msg);
        } catch (Exception e) {
            // 记录异常日志并抛出MqSendException异常
            LOGGER.error("单边发送消息失败", e);
            throw new MqSendException(e);
        }
    }
    
    
    /**
     * 异步发送消息，默认回调函数
     * 本方法用于异步发送消息，并使用默认的回调函数处理发送结果
     *
     * @param topic 消息主题
     * @param tag 消息标签，用于区分不同类别的消息
     * @param keys 消息的键，用于某些特定的路由分发
     * @param content 消息内容
     *
     * @throws MqSendException 如果消息发送失败，则抛出此异常
     */
    public void sendAsyncDefault(String topic, String tag, String keys, String content) {
        try {
            // 构建消息对象
            Message msg = getMessage(topic, tag, keys, content);
            // 异步发送消息，并提供回调函数
            rocketProducer.send(msg, rocketSendCallback);
            // 记录消息日志
            this.logMsg(msg);
        } catch (Exception e) {
            // 记录消息发送失败的错误日志
            LOGGER.error("异步发送消息失败", e);
            // 如果发生异常，抛出自定义的MQ发送异常
            throw new MqSendException(e);
        }
    }
    
    
    /**
     * 异步发送消息到指定的主题和标签
     *
     * @param topic 消息主题
     * @param tag 消息标签，用于进一步划分主题内的消息
     * @param content 消息内容
     * @param keys 用于分区路由的消息键
     * @param sendCallback 回调接口，在消息发送完成后调用
     *
     * 注意：此方法通过异步方式发送消息，不会等待消息发送完成而是立即返回
     * 如果需要同步等待发送结果，请使用 sendSync 方法
     */
    public void sendAsync(String topic, String tag, String content, String keys, SendCallback sendCallback) {
        try {
            // 创建消息对象
            Message msg = getMessage(topic, tag, keys, content);
            // 异步发送消息，并传入回调函数
            rocketProducer.send(msg, sendCallback);
            // 记录消息日志
            this.logMsg(msg);
        } catch (Exception e) {
            // 记录异常日志，并抛出自定义异常
            LOGGER.error("异步发送消息失败", e);
            throw new MqSendException(e);
        }
    }
    
    /**
     * 同步发送消息
     * 此方法将消息发送到指定的主题、标签和键下，适用于需要等待发送结果的场景
     *
     * @param topic 消息主题，用于分类消息
     * @param tag 消息标签，用于进一步细分消息
     * @param keys 消息的键，用于路由消息到特定的队列
     * @param content 消息的具体内容
     * @return SendResult 消息发送的结果，包含消息是否成功发送的信息
     * @throws MqSendException 当消息发送失败时抛出此异常
     */
    public SendResult synSend(String topic, String tag, String keys, String content) {
        try {
            // 创建消息对象
            Message msg = getMessage(topic, tag, keys, content);
            // 发送消息
            SendResult sendResult = rocketProducer.send(msg);
            // 记录消息和发送结果
            this.logMsg(msg, sendResult);
            // 返回发送结果
            return sendResult;
        } catch (Exception e) {
            // 记录发送异常
            LOGGER.error("同步发送消息失败", e);
            // 抛出消息发送异常
            throw new MqSendException(e);
        }
    }
    
    /**
     * 有顺序发送
     * 该方法用于在指定的主题、标签和键下，根据订单ID将消息发送到特定的消息队列中
     * 保证消息在处理时的有序性，适用于需要按特定顺序处理消息的场景
     *
     * @param topic 消息主题，表示消息的类别
     * @param tag 消息标签，用于进一步细分消息的类别
     * @param keys 消息的键，可用于消息的路由
     * @param content 消息内容，表示发送的具体信息
     * @param orderId 订单ID，用于确定消息发送的顺序
     * @return SendResult 发送结果，包含消息发送的状态和相关信息
     * @throws MqSendException 如果消息发送失败，则抛出该自定义异常
     */
    public SendResult orderSend(String topic, String tag, String keys, String content, int orderId) {
        try {
            // 构建消息对象
            Message msg = getMessage(topic, tag, keys, content);
            
            // 发送消息，指定分区顺序消息的分区逻辑
            SendResult sendResult = rocketProducer
                    .send(msg, (List<MessageQueue> mqs, Message message, Object arg) -> {
                                Integer id = (Integer) arg;
                                int index = id % mqs.size();
                                return mqs.get(index);
                            }
                            , orderId);
            
            // 记录消息发送日志
            this.logMsg(msg, sendResult);
            return sendResult;
        } catch (Exception e) {
            // 记录消息发送异常，并抛出自定义异常
            LOGGER.error("有顺序发送消息失败", e);
            throw new MqSendException(e);
        }
    }
    
    /**
     * 构造一个Message对象
     *
     * @param topic 消息主题，用于分类消息
     * @param tag 消息标签，用于业务系统进一步细化消息类型
     * @param keys 消息的键，通常用于唯一标识消息
     * @param content 消息内容，这里将内容转换为字节数组
     * @return 返回构造好的Message对象
     */
    public Message getMessage(String topic, String tag, String keys, String content) {
        // 使用提供的参数构造并返回一个Message对象
        return new Message(topic, tag, keys, content.getBytes());
    }
    
    /**
     * 打印消息topic等参数方便后续查找问题
     */
    private void logMsg(Message message) {
        LOGGER.info("消息队列发送完成:topic={},tag={},msgId={}",
                message.getTopic(),
                message.getTags(),
                message.getKeys());
    }
    
    /**
     * 打印消息topic等参数方便后续查找问题
     */
    private void logMsg(Message message, SendResult sendResult) {
        LOGGER.info("消息队列发送完成:topic={},tag={},msgId={},sendResult={}",
                message.getTopic(),
                message.getTags(),
                message.getKeys(),
                Objects.nonNull(sendResult) ? sendResult : " result is null");
    }
    
    static class RocketSendCallback implements SendCallback {
        /**
         * 当消息发送成功时调用的回调方法
         *
         * @param sendResult 消息发送的结果，包含消息队列的信息和消息ID
         */
        @Override
        public void onSuccess(SendResult sendResult) {
            // 记录消息发送成功的日志，包括主题和消息ID
            LOGGER.info("send message success. topic={}, msgId={}"
                    , sendResult.getMessageQueue().getTopic()
                    , sendResult.getMsgId());
        }
        
        /**
         * 处理异常方法，当发送消息失败时调用
         *
         * @param e 异常对象，可能是MqContextException或其他类型的异常
         */
        @Override
        public void onException(Throwable e) {
            if (e instanceof MqContextException) {
                MqContextException context = (MqContextException) e;
                LOGGER.error("send message failed. topic={}, msgId={}",
                        context.getTopic(),
                        context.getMessageId());
            } else {
                LOGGER.error("send message failed = {}", e.getMessage());
            }
        }
    }
}
