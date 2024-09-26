package com.willjo.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Grio Vino
 * @since 2024-09-26
 **/
@Setter
@Getter
public class MqContextException extends Throwable {

    private String messageId;
    private String topic;
    private MqClientException exception;

    public MqContextException() {
    }
    
}
