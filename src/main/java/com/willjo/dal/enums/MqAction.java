package com.willjo.dal.enums;


/**
 * 消息确认枚举
 *
 * @author Grio Vino
 * @since 2024-09-26
 **/
public enum MqAction {

    //消费成功确认消息
    CommitMessage,

    //稍后继续消费
    ReconsumeLater;

    MqAction() {

    }
}