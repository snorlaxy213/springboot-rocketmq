package com.willjo.mq.readyevent;

import com.willjo.mq.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 创建并启动一个名为"monitor"的线程，持续监控两个队列（priorityQueue和delayQueue）的大小。
 *
 * @author Grio vino
 * @since 2024-09-26
 */
public class MonitorQueueReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorQueueReadyEvent.class);

    // 队列最大值
    public static final int MAX_PRIORITY_QUEUE_SIZE = 1000;

    // 延迟队列最大值
    public static final int MAX_DELAY_QUEUE_SIZE = 1000;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final AtomicBoolean shouldRun = new AtomicBoolean(true); // 控制线程是否继续运行
        final long logIntervalMillis = 60 * 1000; // 日志记录的时间间隔，例如 60 秒
        final AtomicLong lastLoggedTime = new AtomicLong(System.currentTimeMillis());

        new Thread(() -> {
            while (shouldRun.get()) {
                try {
                    synchronized (MessageQueue.priorityQueue) {
                        int size = MessageQueue.priorityQueue.size();
                        if (size > MAX_PRIORITY_QUEUE_SIZE) {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastLoggedTime.get() >= logIntervalMillis) {
                                LOGGER.warn("priorityQueue size:{}", size);
                                lastLoggedTime.set(currentTime);
                            }
                        }
                    }

                    synchronized (MessageQueue.delayQueue) {
                        int size = MessageQueue.delayQueue.size();
                        if (size > MAX_DELAY_QUEUE_SIZE) {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastLoggedTime.get() >= logIntervalMillis) {
                                LOGGER.warn("delayQueue size:{}", size);
                                lastLoggedTime.set(currentTime);
                            }
                        }
                    }

                    Thread.sleep(1000); // 休眠 1 秒，避免 CPU 过度占用
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    break;
                }
            }
        }, "monitor").start();

        // 在某个合适的地方关闭线程
        // shouldRun.set(false);
    }

}