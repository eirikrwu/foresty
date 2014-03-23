package com.foresty.service;

/**
 * Created by EveningSun on 14-3-23.
 */
public class EventServiceException extends ForestyServiceException {
    public EventServiceException(String message) {
        super(message);
    }

    public EventServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
