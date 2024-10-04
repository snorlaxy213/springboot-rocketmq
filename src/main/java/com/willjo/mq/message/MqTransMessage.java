package com.willjo.mq.message;

import com.willjo.dal.entity.MqTransMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Grio vino
 * @since 2024-09-26
 */
@Setter
@Getter
@AllArgsConstructor
public class MqTransMessage implements Serializable {

    private Long id;

    private String topic;

    private String tag;

    private String message;

    private Date createTime;

    private Date updateTime;

    /**
     * 失败次数，根据这个来判断延迟时间和是否继续发送
     */
    private Integer failCount = 0;

    /**
     * 根据MqTransMessageEntity对象创建MqTransMessage对象
     *
     * @param messageEntity MqTransMessageEntity对象，包含消息的所有必要属性
     * @return 返回一个新的MqTransMessage对象
     */
    public static MqTransMessage instance(MqTransMessageEntity messageEntity) {
        // 使用实体类中的属性值初始化MqTransMessage对象，并将重试次数设置为0
        return new MqTransMessage(messageEntity.getId(), messageEntity.getTopic(),
                messageEntity.getTag(),
                messageEntity.getMessage(),
                messageEntity.getCreateTime(),
                messageEntity.getUpdateTime(), 0);
    }

}