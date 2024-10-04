package com.willjo.mq.readyevent;

import com.willjo.mq.message.MqTransMessageDelay;
import com.willjo.mq.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Grio vino
 * @since 2024-09-26
 */
public class TransDelayMessageReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TransDelayMessageReadyEvent.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        new Thread(() -> {
            while (true) {
                try {
                    MqTransMessageDelay messageDelay = MessageQueue.delayQueue.take();
                    logger.info("delay message poll ,message={}", messageDelay);
                    MessageQueue.priorityQueue.put(messageDelay);
                } catch (InterruptedException e) {
                    logger.error("delay message poll error", e);
                }

            }

        }, "delayMessage").start();
    }
}