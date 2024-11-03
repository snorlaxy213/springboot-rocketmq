package com.willjo.mq.constant;

/**
 * @author Grio Vino
 * @since 2024-09-26
 */
public class MqConstant {
    
    /**
     * top
     */
    public static class Top {
        public static final String USER_ORDER_TOPIC = "USER_ORDER_TOPIC";
        public static final String CONSOLE_USER_ORDER_TOPIC = "CONSOLE_USER_ORDER_TOPIC";
    }
    
    /**
     * TAG
     */
    public static class Tag {
        public static final String USER_ORDER_TAG = "USER_ORDER_TAG";
        public static final String CONSOLE_USER_ORDER_TAG = "CONSOLE_USER_ORDER_TAG";
    }
    
    /**
     * consumeGroup 消费者
     */
    public static class ConsumeGroup {
        public static final String USER_ORDER_GROUP = "USER_ORDER_GROUP";
        public static final String CONSOLE_USER_ORDER_GROUP = "CONSOLE_USER_ORDER_GROUP";
    }
}
