package com.willjo.task;

import com.willjo.dal.entity.MqTransMessageEntity;
import com.willjo.mq.RocketMqProducerUtil;
import com.willjo.mq.constant.MessageLockConstant;
import com.willjo.service.MqTransMessageService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * mq 事务消息定时任务
 *
 * @author Grio Vino
 * @since 2024-09-26
 */
@Component
public class MqTransMessageTask {

    private static final Logger logger = LoggerFactory.getLogger(MqTransMessageTask.class);

    @Autowired
    private MqTransMessageService messageService;
    @Autowired
    private RocketMqProducerUtil rocketMqProducerUtil;

//    @Scheduled(fixedDelay = 5 * 1000)
    public void sendMessage() {
        List<MqTransMessageEntity> list = messageService.list();
        LinkedBlockingDeque<Long> successIds = new LinkedBlockingDeque<>();
        // 如果执行期间宕机，那么这里会导致消息重发，单消费端必须要保证幂等
        list.parallelStream().forEach(messageEntity -> {
            String key = MessageFormat.format(MessageLockConstant.LOCK_PREFIX, messageEntity.getId());
            synchronized (key.intern()) {
                SendResult sendResult = rocketMqProducerUtil.synSend(messageEntity.getTopic(), messageEntity.getTag(), "", messageEntity.getMessage());
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    successIds.add(messageEntity.getId());
                }
            }
        });
        // 发送成功删除
        if (!CollectionUtils.isEmpty(successIds)) {
            messageService.removeByIds(successIds);
        }
    }
}