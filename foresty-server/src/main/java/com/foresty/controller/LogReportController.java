package com.foresty.controller;

import com.foresty.model.Event;
import com.foresty.model.EventGroup;
import com.foresty.model.Log;
import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
@Controller
@RequestMapping("")
public class LogReportController {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private EventRepository eventRepository;

    @RequestMapping(value = "/event-groups", method = RequestMethod.GET)
    public @ResponseBody List<EventGroup> getEventGroups() {
        List<EventGroup> eventGroups = Lists.newArrayList();
        for (String name : this.eventRepository.getEventNames()) {
            EventGroup eventGroup = new EventGroup();
            eventGroup.setName(name);
            eventGroups.add(eventGroup);
        }

        return eventGroups;
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public @ResponseBody
    List<Event> getEventsByCriterion(@RequestParam(value = "name", required = false) String eventName,
                                     @RequestParam(value = "level", required = false) Integer minHigestLevel) {
        EventRepository.EventCriteria criteria = new EventRepository.EventCriteria();
        criteria.setName(eventName);
        criteria.setMinHighestLevel(minHigestLevel);
        criteria.setOrderBy("startTime");
        criteria.setOrderDesc(true);

        return this.eventRepository.getEventsByCriterion(criteria);
    }

    @RequestMapping(value = "/events/{eventId}", method = RequestMethod.GET)
    public @ResponseBody List<Log> getLogForEvents(@PathVariable String eventId) {
        return this.logRepository.findLogByEventId(eventId);
    }
}
