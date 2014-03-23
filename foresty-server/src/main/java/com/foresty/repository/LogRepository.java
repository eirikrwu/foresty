package com.foresty.repository;

import com.foresty.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public interface LogRepository extends JpaRepository<Log, Long> {
    @Query("SELECT l FROM Log l WHERE l.event.id = ?1 ORDER BY l.timestamp")
    public List<Log> findLogByEventId(String eventId);

    @Modifying
    @Query("DELETE FROM Log l WHERE l.event.id = ?1")
    public void deleteLogByEventId(String eventId);
}
