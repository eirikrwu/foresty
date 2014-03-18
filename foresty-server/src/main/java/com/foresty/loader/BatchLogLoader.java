package com.foresty.loader;

/**
 * Created by ericwu on 3/15/14.
 */
public interface BatchLogLoader {
    public void loadLog(Object object, String type) throws BatchLogLoaderException;
}
