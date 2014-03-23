package com.foresty.service.impl;

import com.foresty.DomainConfig;
import com.foresty.model.Event;
import com.foresty.model.Log;
import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.foresty.service.EventService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by EveningSun on 14-3-23.
 */
@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainConfig.class)
public class EventServiceImplTest {
    @Autowired
    private EventService eventService;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private EventRepository eventRepository;

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
    public void testDeleteEventById() throws Exception {
        Event event = new Event();
        event.setId("event1");
        Log log = new Log();
        log.setMessage("message");
        log.setEvent(event);
        this.eventRepository.save(event);
        this.logRepository.save(log);

        this.eventService.deleteEventById(event.getId());
    }
}
