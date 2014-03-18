package com.foresty.repository;

import com.foresty.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public interface LogRepository extends JpaRepository<Log, Long> {
    @Query("SELECT l FROM Log l WHERE l.name = ?1")
    public Log getLogByName(String name);

    @Query("SELECT l FROM Log l WHERE l.parent.id = ?1")
    public List<Log> getLogsByParentId(long id);
}
