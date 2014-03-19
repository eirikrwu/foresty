package com.foresty.client.appender;

import com.google.common.base.Preconditions;
import org.apache.log4j.MDC;

import java.util.UUID;

/**
 * Created by EveningSun on 14-3-19.
 */
public class ForestyLog4j {
    public static void beginEvent(String eventName) {
        Preconditions.checkNotNull(eventName);
        Preconditions.checkArgument(!eventName.trim().isEmpty());

        MDC.put(ForestyAppender.EVENT_KEY, eventName);
        MDC.put(ForestyAppender.EVENT_ID_KEY, UUID.randomUUID().toString());
    }

    public static void exitEvent() {
        MDC.remove(ForestyAppender.EVENT_ID_KEY);
        MDC.remove(ForestyAppender.EVENT_KEY);
    }
}
