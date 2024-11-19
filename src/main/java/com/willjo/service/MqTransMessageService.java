package com.willjo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.willjo.dal.entity.MqTransMessageEntity;

public interface MqTransMessageService extends IService<MqTransMessageEntity> {

    /**
     * 获取实体
     *
     * @param id 实体ID
     * @return 实体
     */
    MqTransMessageEntity selectById(Long id);

    /**
     * 发送本地事务消息
     *
     * @param topic   topic
     * @param tag     tag
     * @param content content
     * @return true/false
     * @throws IllegalArgumentException topic 或者content 为空的时候抛出参数异常
     */
    Boolean transSendMsg(String topic, String tag, String content);

}