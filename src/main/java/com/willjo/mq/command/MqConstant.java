package com.willjo.mq.command;

/**
 * @author Grio Vino
 * @since 2024-09-26
 */
public class MqConstant {

    /**
     * top
     */
    public static class Top {
        public static final String USER_ORDER_TOPIC = "USER_ORDER_TOPIC_TEST";
        public static final String PAY_TOPIC = "PAY_TOPIC";
    }

    /**
     * TAG
     */
    public static class Tag {
        public static final String USER_TAG = "USER_TAG";
        public static final String PAY_TAG = "PAY_TAG";
    }

    /**
     * consumeGroup 消费者
     */
    public static class ConsumeGroup {
        public static final String PAY_GROUP = "PAY_GROUP";
        public static final String USER_ORDER_GROUP = "USER_ORDER_GROUP";
    }
}