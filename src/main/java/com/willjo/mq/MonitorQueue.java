package com.willjo.mq;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 监控队列：监控队列大小，避免队列过大导致内存溢出
 *
 * @author Grio vino
 * @since 2024-09-26
 */
public class MonitorQueue implements ApplicationListener<ApplicationReadyEvent> {

    public static final int MAX_PRIORITY_QUEUE_SIZE = 1000;

    public static final int MAX_DELAY_QUEUE_SIZE = 1000;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        new Thread(() -> {
            while (true) {
                int size = MessageQueue.priorityQueue.size();
                if (size > MAX_PRIORITY_QUEUE_SIZE) {
                    System.out.println("priorityQueue size:" + size);
                }
                size = MessageQueue.delayQueue.size();
                if (size > MAX_DELAY_QUEUE_SIZE) {
                    System.out.println("delayQueue size:" + size);
                }
            }
        }, "monitor").start();
    }
}
