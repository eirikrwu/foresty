package com.foresty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by EveningSun on 14-3-23.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ControllerRuntimeException extends RuntimeException {
    public ControllerRuntimeException(String message) {
        super(message);
    }

    public ControllerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
