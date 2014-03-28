package com.foresty.controller;

import com.foresty.model.Event;
import com.foresty.model.EventGroup;
import com.foresty.model.Log;
import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.foresty.service.EventService;
import com.foresty.service.EventServiceException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private EventService eventService;

    @RequestMapping(value = "/event-groups", method = RequestMethod.GET)
    public @ResponseBody List<EventGroup> getEventGroups() {
        try {
            List<EventGroup> eventGroups = Lists.newArrayList();
            for (String name : this.eventRepository.getEventNames()) {
                EventGroup eventGroup = new EventGroup();
                eventGroup.setName(name);
                eventGroups.add(eventGroup);
            }

            return eventGroups;
        } catch (DataAccessException e) {
            throw new ControllerRuntimeException("Error occurred while trying to get event groups: " + e.getMessage(),
                    e);
        }
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public @ResponseBody
    PagedResults<Event> getEventsByCriterion(@RequestParam(value = "name", required = false) String eventName,
                                             @RequestParam(value = "queries[level]", required = false) Integer minHigestLevel,
                                             @RequestParam(value = "page", required = true) Integer pageNumber,
                                             @RequestParam(value = "perPage", required = true) Integer pageSize) {
        // the request page is 1-based
        pageNumber--;
        Preconditions.checkArgument(pageNumber >= 0);
        Preconditions.checkArgument(pageSize >= 0 && pageSize <= 10000);

        EventRepository.EventCriteria criteria = new EventRepository.EventCriteria();
        criteria.setName(eventName);
        criteria.setMinHighestLevel(minHigestLevel);
        criteria.setOrderBy("startTime");
        criteria.setOrderDesc(true);

        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        try {
            Page<Event> page = this.eventRepository.getEventsByCriterion(criteria, pageRequest);

            return new PagedResults(page);
        } catch (DataAccessException e) {
            throw new ControllerRuntimeException("Error occurred while trying to get events: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/events/{eventId}", method = RequestMethod.GET)
    public @ResponseBody List<Log> getLogForEvents(@PathVariable String eventId) {
        try {
            return this.logRepository.findLogByEventId(eventId);
        } catch (DataAccessException e) {
            throw new ControllerRuntimeException("Error occurred while trying to get log for event#" + eventId, e);
        }
    }

    @RequestMapping(value = "/events/{eventId}", method = RequestMethod.DELETE)
    public @ResponseBody Result deleteEvents(@PathVariable String eventId) {
        try {
            this.eventService.deleteEventById(eventId);
        } catch (EventServiceException e) {
            throw new ControllerRuntimeException("Error occurred while trying to delete event#" + eventId, e);
        }

        return Result.OK;
    }
}
