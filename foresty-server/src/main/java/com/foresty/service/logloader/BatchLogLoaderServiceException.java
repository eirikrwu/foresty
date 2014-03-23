package com.foresty.service.logloader;

import com.foresty.service.ForestyServiceException;

/**
 * Created by ericwu on 3/15/14.
 */
public class BatchLogLoaderServiceException extends ForestyServiceException {
    public BatchLogLoaderServiceException(String message) {
        super(message);
    }

    public BatchLogLoaderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
