package com.willjo.exception;

/**
 * mq 消息发送异常类
 *
 * @author Grio Vino
 * @since 2024-09-26
 */
public class MqSendException extends RuntimeException {

    private static final long serialVersionUID = -1014344856325540529L;

    public MqSendException() {
        super();
    }

    public MqSendException(String message) {
        super(message);
    }

    public MqSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqSendException(Throwable cause) {
        super(cause);
    }
}
