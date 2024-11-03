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
        public static final String CONSOLE_USER_ORDER_TOPIC = "CONSOLE_USER_ORDER_TOPIC";
    }
    
    /**
     * TAG
     */
    public static class Tag {
        public static final String USER_TAG = "USER_TAG";
        public static final String CONSOLE_USER_TAG = "CONSOLE_USER_TAG";
    }
    
    /**
     * consumeGroup 消费者
     */
    public static class ConsumeGroup {
        public static final String USER_GROUP = "USER_GROUP";
        public static final String CONSOLE_USER_GROUP = "CONSOLE_USER_GROUP";
    }
}
