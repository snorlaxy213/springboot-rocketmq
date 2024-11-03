package com.willjo.mq;

import com.google.common.collect.Maps;
import com.willjo.annotation.RocketMqListener;
import com.willjo.annotation.RocketMqOrderListener;
import com.willjo.config.RocketMqProperties;
import com.willjo.enums.MqAction;
import com.willjo.mq.listener.MessageListener;
import com.willjo.mq.listener.MessageOrderListener;
import com.willjo.util.GeneratorId;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;


/**
 * 消费者启动类
 *
 * @author Grio Vino
 * @since 2024-09-26
 **/
public class RocketMqConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqConsumer.class);

    public ApplicationContext context;
    private volatile boolean init = false;
    private final RocketMqProperties configuration;
    private Map<String, DefaultMQPushConsumer> consumerMap;

    public RocketMqConsumer(RocketMqProperties configuration, ApplicationContext context) {
        this.context = context;
        this.configuration = configuration;
    }

    public synchronized void start() throws Exception {
        if (this.init) {
            LOGGER.warn("请不要重复初始化RocketMQ消费者");
            return;
        }
        this.consumerMap = Maps.newConcurrentMap();
        initializeConsumer(this.consumerMap);
        init = true;
    }

    /**
     * 初始化消费者，同项目内不允许对同一个topic多次加载
     *
     * @param map 存储消费者
     */
    private void initializeConsumer(Map<String, DefaultMQPushConsumer> map) throws Exception {

        Map<String, String> topicMap = Maps.newHashMap();

        Map<String, String> consumerGroupMap = Maps.newHashMap();
        //初始化普通消息消费者
        initializeUserConsumer(map, topicMap, consumerGroupMap);

        //初始化有序消息消费者
        initializeUserOrderConsumer(map, topicMap, consumerGroupMap);

        consumerMap.forEach((key, consumer) -> {
            try {
                String instanceName = System.currentTimeMillis() + GeneratorId.nextFormatId();
                consumer.setInstanceName(instanceName);
                consumer.start();
                LOGGER.info("自建RocketMQ 成功加载 Topic-tag:{}", key);
            } catch (MQClientException e) {
                LOGGER.error(String.format("自建RocketMQ 加载失败 Topic-tag:%s", key), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        LOGGER.info("--------------成功初始化所有消费者到自建mq--------------");
    }

    /**
     * 初始化普通消息消费者
     *
     * @param map 消费者映射表，用于存储初始化后的消费者实例
     * @param topicMap 主题映射表，用于存储主题与类名的对应关系
     * @param consumerGroupMap 消费组映射表，用于存储消费组与类名的对应关系
     * @throws MQClientException 如果消费者初始化失败，抛出此异常
     */
    private void initializeUserConsumer(Map<String, DefaultMQPushConsumer> map,
                                        Map<String, String> topicMap, Map<String, String> consumerGroupMap)
            throws MQClientException {
        // 获取所有带有RocketMqListener注解的Bean
        Map<String, Object> beansWithAnnotationMap = context.getBeansWithAnnotation(RocketMqListener.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotationMap.entrySet()) {
            // 获取到实例对象的class信息
            Class<?> classIns = entry.getValue().getClass();
            // 获取RocketMqListener注解信息
            RocketMqListener rocketMqListenerAnnotation = classIns.getDeclaredAnnotation(RocketMqListener.class);
            String topic = rocketMqListenerAnnotation.topic();
            String tag = rocketMqListenerAnnotation.tag();
            String consumerGroup = rocketMqListenerAnnotation.consumerGroup();
            // 验证主题和消费组的合法性
            validate(topicMap, consumerGroupMap, classIns, topic, consumerGroup);
            // 创建消费者实例（消费者等待Broker把消息推送过来）
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            //设置名称服务器地址
            consumer.setNamesrvAddr(this.configuration.getNamesrvAddr());
            // 订阅主题和标签
            consumer.subscribe(topic, tag);
            //注册消费回调
            consumer.registerMessageListener((MessageListenerConcurrently) (msgList, context) -> {
                try {
                    for (MessageExt msg : msgList) {
                        // 获取消息监听器实例
                        MessageListener listener = (MessageListener) entry.getValue();
                        // 调用监听器的消费方法
                        MqAction action = listener.consume(msg, context);
                        // 根据消费结果决定是否重新消费
                        switch (action) {
                            case ReconsumeLater:
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            default:
                        }
                    }
                } catch (Exception e) {
                    // 消费失败时记录日志，并返回需要重新消费的状态
                    LOGGER.error("消费失败", e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                // 消费成功后返回消费成功的状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            // 将主题和类名的对应关系存入映射表
            topicMap.put(topic, classIns.getSimpleName());
            // 将消费组和类名的对应关系存入映射表
            consumerGroupMap.put(consumerGroup, classIns.getSimpleName());
            // 将消费者实例按照主题和标签的组合键存入映射表
            map.put(String.format("%s-%s", topic, tag), consumer);
        }
    }

    private void validate(Map<String, String> topicMap,
                          Map<String, String> consumerGroupMap,
                          Class<?> classIns, String topic, String consumerGroup) {
        if (StringUtils.isBlank(topic)) {
            throw new RuntimeException(classIns.getSimpleName() + ":topic不能为空");
        }
        if (StringUtils.isBlank(consumerGroup)) {
            throw new RuntimeException(classIns.getSimpleName() + ":consumerGroup不能为空");
        }

        if (topicMap.containsKey(topic)) {
            throw new RuntimeException(
                    String.format("Topic:%s 已经由%s监听 请勿重复监听同一Topic", topic, classIns.getSimpleName()));
        }

        if (consumerGroupMap.containsKey(consumerGroup)) {
            throw new RuntimeException(String
                    .format("consumerGroup:%s 已经由%s监听 请勿重复监听同一consumerGroup", consumerGroup,
                            classIns.getSimpleName()));
        }
    }

    /**
     * 初始化有序消息消费者
     * 该方法用于初始化RocketMQ的有序消息消费者，包括解析注解、创建消费者实例、设置消费者属性和注册消息监听器
     *
     * @param map 消费者实例的映射表，key为消费者标识，value为消费者实例
     * @param topicMap 主题映射表，key为主题，value为主题对应的类名
     * @param consumerGroupMap 消费者组映射表，key为消费者组，value为消费者组对应的类名
     * @throws MQClientException 初始化消费者时可能抛出的异常
     */
    private void initializeUserOrderConsumer(Map<String, DefaultMQPushConsumer> map,
                                             Map<String, String> topicMap,
                                             Map<String, String> consumerGroupMap) throws MQClientException {
        // 获取所有带有RocketMqOrderListener注解的Bean
        Map<String, Object> beansWithAnnotationMap = context.getBeansWithAnnotation(RocketMqOrderListener.class);

        for (Map.Entry<String, Object> entry : beansWithAnnotationMap.entrySet()) {
            // 获取到实例对象的class信息
            Class<?> classIns = entry.getValue().getClass();
            // 通过注解获取消息主题、标签和消费者组
            RocketMqOrderListener rocketMqListenerAnnotation = classIns.getDeclaredAnnotation(RocketMqOrderListener.class);
            String topic = rocketMqListenerAnnotation.topic();
            String tag = rocketMqListenerAnnotation.tag();
            String consumerGroup = rocketMqListenerAnnotation.consumerGroup();
            // 校验主题和消费者组的合法性
            validate(topicMap, consumerGroupMap, classIns, topic, consumerGroup);
            // 创建消费者实例（消费者等待Broker把消息推送过来）
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            //设置名称服务器地址
            consumer.setNamesrvAddr(this.configuration.getNamesrvAddr());
            // 订阅指定的主题和标签
            consumer.subscribe(topic, tag);
            // 注册消费回调
            consumer.registerMessageListener((MessageListenerOrderly) (msgList, context) -> {
                try {
                    for (MessageExt msg : msgList) {
                        // 如果启用了调试日志，则记录消息内容
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("consume msg={}", msg);
                        }
                        // 获取消息监听器实例并调用消费方法
                        MessageOrderListener listener = (MessageOrderListener) entry.getValue();
                        MqAction action = listener.consume(msg, context);
                        // 根据消费结果决定是否重新消费
                        switch (action) {
                            case ReconsumeLater:
                                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                            default:
                        }
                    }
                } catch (Exception e) {
                    // 如果消费过程中发生异常，则记录错误并返回重新消费状态
                    LOGGER.error("消费失败", e);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                // 消费成功
                return ConsumeOrderlyStatus.SUCCESS;
            });

            // 更新主题和消费者组的映射表
            topicMap.put(topic, classIns.getSimpleName());
            consumerGroupMap.put(consumerGroup, classIns.getSimpleName());
            // 将消费者实例存入映射表
            map.put(String.format("%s-%s", topic, tag), consumer);
        }
    }
}