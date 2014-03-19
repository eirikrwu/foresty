package com.foresty.repository;

import com.foresty.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by EveningSun on 14-3-19.
 */
public interface EventRepository extends JpaRepository<Event, String> {
    @Query("SELECT DISTINCT (e.name) FROM Event e")
    public List<String> getEventNames();

    @Query("SELECT e FROM Event e WHERE e.name = ?1 ORDER BY e.startTime")
    public List<Event> getEventsByName(String name);
}
