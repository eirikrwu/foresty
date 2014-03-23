package com.foresty.service.logloader;

/**
 * Created by ericwu on 3/15/14.
 */
public interface BatchLogLoaderService {
    public void loadLog(Object object, String type) throws BatchLogLoaderServiceException;
}
