package com.willjo.mq.listener;


import com.willjo.annotation.RocketMqListener;
import com.willjo.enums.MqAction;
import com.willjo.mq.constant.MqConstant;
import com.willjo.util.MqMsgConvertUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RocketMqListener(topic = MqConstant.Top.USER_ORDER_TOPIC, consumerGroup = MqConstant.ConsumeGroup.USER_GROUP)
public class MqUserMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqUserMessageListener.class);

    @Override
    public MqAction consume(MessageExt message, ConsumeConcurrentlyContext context) {
        String msg = null;
        try {
            msg = MqMsgConvertUtil.bytes2String(message.getBody(), "UTF-8");

            LOGGER.info("MsgId:{},MQ消费,Topic:{},Tag:{}，Body:{}", message.getMsgId(), message.getTopic(), message.getTags(), msg);
        } catch (Exception e) {
            LOGGER.error("MsgId:{},应用MQ消费失败,Topic:{},Tag:{}，Body:{},异常信息:{}", message.getMsgId(), message.getTopic(), message.getTags(), msg, e.toString());
            throw e;
        }
        return MqAction.CommitMessage;
    }


}