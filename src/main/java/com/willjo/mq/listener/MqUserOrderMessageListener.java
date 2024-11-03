package com.willjo.mq.listener;


import com.willjo.annotation.RocketMqOrderListener;
import com.willjo.enums.MqAction;
import com.willjo.mq.constant.MqConstant;
import com.willjo.util.MqMsgConvertUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RocketMqOrderListener(topic = MqConstant.Top.PAY_TOPIC, consumerGroup = MqConstant.ConsumeGroup.PAY_GROUP)
public class MqUserOrderMessageListener implements MessageOrderListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqUserOrderMessageListener.class);

    @Override
    public MqAction consume(MessageExt message, ConsumeOrderlyContext context) {
        String msg = null;
        try {
            msg = MqMsgConvertUtil.bytes2String(message.getBody(), "UTF-8");

            LOGGER.info("MsgId:{},MQ消费,Topic:{},Tag:{}，Body:{}", message.getMsgId(), message.getTopic(), message.getTags(), msg);
        } catch (Exception e) {
            LOGGER.error("MsgId:{},应用MQ消费失败,Topic:{},Tag:{}，Body:{},异常信息:{}",
                    message.getMsgId(), message.getTopic(), message.getTags(), msg, e);
            throw e;
        }
        return MqAction.CommitMessage;
    }
    
    
}