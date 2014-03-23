package com.foresty.service.logloader.impl;

import com.foresty.DomainConfig;
import com.foresty.service.logloader.BatchLogLoaderService;
import com.foresty.service.logloader.parser.impl.DefaultLogMessageParser;
import com.foresty.model.Event;
import com.foresty.model.Log;
import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.google.common.base.Joiner;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by EveningSun on 14-3-19.
 */
@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainConfig.class)
public class StringLogLoaderTest {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    @Qualifier("stringLogLoaderService")
    private BatchLogLoaderService batchLogLoaderService;

    @Before
    public void setUp() throws Exception {
        this.logRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        this.logRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

    @Test
    public void testLoadLog() throws Exception {
        String log1 = log("log message 1", 1, Level.DEBUG, "event1", "id1");
        String log2 = log("log message 2", 100000, Level.DEBUG, "event1", "id1");
        String log3 = log("log message 3", 200000, Level.DEBUG, "event1", "id1");
        this.batchLogLoaderService
                .loadLog(Joiner.on(StringLogLoaderService.LOG_MESSAGE_SEPARATOR).join(log1, log2, log3), "default");

        List<Log> logs = this.logRepository.findAll();
        Assert.assertEquals("Should find 3 log message.", 3, logs.size());
        List<Event> events = this.eventRepository.findAll();
        Assert.assertEquals("Should find 1 event.", 1, events.size());
    }

    @Test
    public void testLoadLog_SameEventInDifferentSession() throws Exception {
        String log1 = log("log message 1", 1, Level.DEBUG, "event1", "id1");
        String log2 = log("log message 2", 100000, Level.DEBUG, "event1", "id1");
        String log3 = log("log message 3", 200000, Level.DEBUG, "event1", "id1");
        this.batchLogLoaderService
                .loadLog(Joiner.on(StringLogLoaderService.LOG_MESSAGE_SEPARATOR).join(log1, log2, log3), "default");
        String log4 = log("log message 4", 300000, Level.WARN, "event1", "id1");
        String log5 = log("log message 5", 400000, Level.DEBUG, "event1", "id1");
        this.batchLogLoaderService
                .loadLog(Joiner.on(StringLogLoaderService.LOG_MESSAGE_SEPARATOR).join(log4, log5), "default");

        List<Log> logs = this.logRepository.findAll();
        Assert.assertEquals("Should find 5 log message.", 5, logs.size());
        List<Event> events = this.eventRepository.findAll();
        Assert.assertEquals("Should find 1 event.", 1, events.size());
    }

    private String log(String message, long timestamp, Level level, String eventName, String eventId) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(DefaultLogMessageParser.PATH_PREFIX);
        sb.append(new Date(timestamp).getTime()).append(DefaultLogMessageParser.PATH_SAPERATOR);
        sb.append(level.toInt()).append(DefaultLogMessageParser.PATH_SAPERATOR);
        sb.append(eventName).append(DefaultLogMessageParser.PATH_SAPERATOR);
        sb.append(eventId).append(DefaultLogMessageParser.PATH_SAPERATOR);
        return sb.toString();
    }
}
