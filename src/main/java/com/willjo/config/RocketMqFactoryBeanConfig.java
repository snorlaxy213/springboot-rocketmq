package com.willjo.config;

import com.willjo.mq.RocketMqConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 创建生产者和消费者的工厂bean
 *
 * @author lizhuo
 * @since 2019/1/4 下午10:14
 **/
public class RocketMqFactoryBeanConfig {

    /**
     * 创建RocketMq消费者
     *
     * @param configuration RocketMq配置属性，用于初始化消费者
     * @param context 应用上下文，用于消费者在Spring环境中的集成
     * @return 返回创建的RocketMq消费者实例
     * @throws Exception 如果创建过程中发生错误，抛出异常
     */
    @Bean
    public RocketMqConsumer createConsumer(RocketMqProperties configuration,
        ApplicationContext context) {
        return new RocketMqConsumer(configuration, context);
    }

    /**
     * 创建并启动一个默认的RocketMQ生产者
     *
     * @param configuration RocketMQ配置属性对象，用于获取配置信息
     * @return 返回一个启动了的DefaultMQProducer实例
     * @throws Exception 如果配置错误可能会抛出异常
     * 该方法首先检查rocketmq.nameSrvAddress参数是否必需，然后使用RocketMqProperties配置
     * 创建DefaultMQProducer实例，设置Name Server地址，设置实例名称为当前时间毫秒数，
     * 并启动生产者实例最后返回该实例如果Name Server地址未配置，则抛出异常
     */
    @Bean
    public DefaultMQProducer defaultProducer(RocketMqProperties configuration) throws Exception {
        // 检查Name Server地址是否配置，是的话则抛出异常
        if (configuration.getNamesrvAddr() == null) {
            throw new IllegalArgumentException("rocketmq.nameSrvAddress 是必须的参数");
        }
        // 创建DefaultMQProducer实例
        DefaultMQProducer producer = new DefaultMQProducer(configuration.getProducerId());
        // 设置Name Server地址
        producer.setNamesrvAddr(configuration.getNamesrvAddr());
        // 设置实例名称为当前时间毫秒数，确保唯一性
        producer.setInstanceName(System.currentTimeMillis() + "");
        // 启动生产者实例
        producer.start();
        // 返回启动了的生产者实例
        return producer;
    }


}
