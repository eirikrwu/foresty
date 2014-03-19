package com.foresty.controller;

import com.foresty.loader.impl.StringLogLoader;
import com.google.common.collect.Maps;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.Map;

/**
 * Created by ericwu on 3/16/14.
 */
public class LogCollectionControllerDemo {
    @Test
    public void testSendLog() throws Exception {
        String logs = "log message 1 __1000000,,1,,request,,log" + StringLogLoader.LOG_MESSAGE_SEPARATOR
                + "log message 1 __1000000,,1,,request,,test" + StringLogLoader.LOG_MESSAGE_SEPARATOR
                + "log message 1 __1000000,,1,,request,,log" + StringLogLoader.LOG_MESSAGE_SEPARATOR
                + "log message 2 __2000000,,2,,quartz,,job1";
        Map<String, String> data = Maps.newHashMap();
        data.put("log", logs);
        data.put("type", "default");

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(
                Request.Put("http://127.0.0.1:8080/q/log").addHeader("Content-type", "application/json")
                        .bodyByteArray(mapper.writeValueAsBytes(data)).execute().returnContent().asString());
    }
}
