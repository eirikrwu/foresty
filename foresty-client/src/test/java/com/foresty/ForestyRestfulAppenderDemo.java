package com.foresty;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.junit.Test;

/**
 * Created by ericwu on 3/16/14.
 */
public class ForestyRestfulAppenderDemo {
    @Test
    public void testLog() {
        Logger logger = Logger.getLogger(ForestyRestfulAppenderDemo.class);
        MDC.put("__node1", "http");
        MDC.put("__node2", "/test");
        logger.info("log message 1");
        logger.info("log message 2");
        logger.info("log message 3");
    }
}
