package com.foresty.repository;

import com.foresty.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by EveningSun on 14-3-20.
 */
public interface EventRepositoryCustom {
    public Page<Event> getEventsByCriterion(EventRepository.EventCriteria criteria, Pageable pageable);
}
