package com.foresty.repository;

import com.foresty.model.Event;

import java.util.List;

/**
 * Created by EveningSun on 14-3-20.
 */
public interface EventRepositoryCustom {
    public List<Event> getEventsByCriterion(EventRepository.EventCriteria criteria);
}
