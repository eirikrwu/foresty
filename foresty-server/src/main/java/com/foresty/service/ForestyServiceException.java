package com.foresty.service;

/**
 * Created by EveningSun on 14-3-23.
 */
public class ForestyServiceException extends Exception {
    public ForestyServiceException(String message) {
        super(message);
    }

    public ForestyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
