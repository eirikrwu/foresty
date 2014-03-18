package com.foresty.loader;

/**
 * Created by ericwu on 3/15/14.
 */
public class BatchLogLoaderException extends Exception {
    public BatchLogLoaderException(String message) {
        super(message);
    }

    public BatchLogLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
