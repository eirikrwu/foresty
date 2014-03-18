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
    public static final String NODE_NAME_MDC_PREFIX = "__node";
    public static final String PATH_PREFIX = "__";
    public static final String PATH_SAPERATOR = ",,";

    private final List<String> cachedLoggings = Lists.newArrayList();

    private String forestyUrl;
    private int flushThreshold;

    @Override
    protected void append(LoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(getLayout().format(event).trim());
        sb.append(PATH_PREFIX);
        sb.append(event.getTimeStamp());

        // append tags
        for (int i = 1; ; i++) {
            String mdcKey = NODE_NAME_MDC_PREFIX + i;
            Object mdcValue = event.getMDC(mdcKey);
            if (mdcValue != null && mdcValue instanceof String) {
                sb.append(PATH_SAPERATOR);
                sb.append(mdcValue);
            } else {
                break;
            }
        }

        this.cachedLoggings.add(sb.toString());
        if (this.cachedLoggings.size() >= this.flushThreshold) {
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

    private void flush() {
        String logs = Joiner.on("\n").join(this.cachedLoggings);

        Map<String, String> logRequest = Maps.newHashMap();
        logRequest.put("log", logs);
        logRequest.put("type", "default");

        try {
            Request.Put(this.forestyUrl + "/log").addHeader("Content-type", "application/json")
                    .bodyByteArray(new ObjectMapper().writeValueAsBytes(logRequest)).execute().discardContent();
            this.cachedLoggings.clear();
        } catch (IOException e) {
            //FIXME: handle error
            e.printStackTrace();
        }
    }
}
