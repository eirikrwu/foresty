package com.foresty.service.impl;

import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.foresty.service.EventService;
import com.foresty.service.EventServiceException;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by EveningSun on 14-3-23.
 */
@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LogRepository logRepository;

    @Override
    @Transactional
    public void deleteEventById(String eventId) throws EventServiceException {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkArgument(!eventId.trim().isEmpty());

        this.logRepository.deleteLogByEventId(eventId);
        this.eventRepository.delete(eventId);
    }
}
