package com.willjo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willjo.dal.entity.MqTransMessageEntity;
import com.willjo.dal.mapper.MessageMapper;
import com.willjo.mq.MessageQueue;
import com.willjo.service.MqTransMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 服务实现类
 *
 * @author Grio Vino
 * @since 2024-09-26
 */
@Service
public class MqTransMessageServiceImpl extends ServiceImpl<MessageMapper, MqTransMessageEntity> implements MqTransMessageService {

    @Override
    public MqTransMessageEntity selectById(Long id) {
        return super.getById(id);
    }

    @Override
    public Boolean transSendMsg(String topic, String tag, String content) {
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic 不能为空");
        }
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("content 不能为空");
        }
        // 持久化数据
        MqTransMessageEntity msg = new MqTransMessageEntity();
        msg.setTopic(topic)
                .setTag(tag)
                .setMessage(content)
                .setCreateTime(new Date());
        super.save(msg);
        // 放入优先级队列
        return MessageQueue.putInPriorityQueue(msg);
    }
}