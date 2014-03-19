package com.foresty.loader.parser;

import com.foresty.model.Log;

/**
 * Created by ericwu on 3/15/14.
 */
public interface LogMessageParser {
    public Log parseLogMessage(String message);
}
