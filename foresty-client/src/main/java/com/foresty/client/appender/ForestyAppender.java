package com.foresty.client.appender;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ericwu on 3/15/14.
 */
public class ForestyAppender extends AppenderSkeleton {
    public static final String LOG_MESSAGE_SEPARATOR = "__/\n/__";
    public static final String PATH_PREFIX = "__begin-foresty__";
    public static final String PATH_SAPERATOR = ",,";

    public static final String EVENT_KEY = "__forestyEvent";
    public static final String EVENT_ID_KEY = "__forestyEventId";

    public static final String DEFAULT_EVENT_NAME = "__ungroupedLog";
    public static final String DEFAULT_EVENT_ID = "__defaultUngroupedEventId";

    // FIXME: don't used static, use a seperate cache and submit executor instead
    private static final List<String> CACHED_LOGGINGS = Lists.newArrayList();

    private String forestyUrl;
    private int flushThreshold = 1;
    private boolean eventLogOnly = true;

    @Override
    protected void append(LoggingEvent event) {
        // check foresty event first
        String eventName = (String) event.getMDC(EVENT_KEY);
        String eventId = (String) event.getMDC(EVENT_ID_KEY);
        if (eventName == null && !this.eventLogOnly) {
            eventName = DEFAULT_EVENT_NAME;
            eventId = DEFAULT_EVENT_ID;
        }

        // if there is no event and this appender is event log only, ignore the log message
        if (eventName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLayout().format(event).trim());
            sb.append(PATH_PREFIX);

            // first element is timestamp
            sb.append(event.getTimeStamp());
            sb.append(PATH_SAPERATOR);
            // send element level
            sb.append(event.getLevel().toInt());
            sb.append(PATH_SAPERATOR);
            // event name
            sb.append(eventName);
            sb.append(PATH_SAPERATOR);
            // event id
            sb.append(eventId);
            sb.append(PATH_SAPERATOR);

            // add event string to cache
            this.CACHED_LOGGINGS.add(sb.toString());
        }

        if (this.CACHED_LOGGINGS.size() >= this.flushThreshold) {
            flush();
        }
    }

    @Override
    public void close() {
        flush();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public String getForestyUrl() {
        return forestyUrl;
    }

    public void setForestyUrl(String forestyUrl) {
        this.forestyUrl = forestyUrl;
    }

    public int getFlushThreshold() {
        return flushThreshold;
    }

    public void setFlushThreshold(int flushThreshold) {
        this.flushThreshold = flushThreshold;
    }

    public boolean isEventLogOnly() {
        return eventLogOnly;
    }

    public void setEventLogOnly(boolean eventLogOnly) {
        this.eventLogOnly = eventLogOnly;
    }

    private void flush() {
        String logs = Joiner.on(LOG_MESSAGE_SEPARATOR).join(this.CACHED_LOGGINGS);

        Map<String, String> logRequest = Maps.newHashMap();
        logRequest.put("log", logs);
        logRequest.put("type", "default");

        try {
            Request.Put(this.forestyUrl + "/log").addHeader("Content-type", "application/json")
                    .bodyByteArray(new ObjectMapper().writeValueAsBytes(logRequest)).execute().discardContent();
            this.CACHED_LOGGINGS.clear();
        } catch (IOException e) {
            //FIXME: handle error
            e.printStackTrace();
        }
    }
}
