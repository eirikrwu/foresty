package com.foresty;

import com.foresty.client.appender.ForestyAppender;
import com.foresty.client.appender.ForestyLog4j;
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

        ForestyLog4j.beginEvent("event1");
        logger.info("log message 1");
        logger.info("log message 2");
        logger.info("log message 3");
        ForestyLog4j.exitEvent();

        ForestyLog4j.beginEvent("event2");
        logger.info("log message 4");
        logger.info("log message 5");
        logger.info("log message 6");
        ForestyLog4j.exitEvent();
    }
}
