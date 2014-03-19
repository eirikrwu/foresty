package com.foresty.repository;

import com.foresty.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public interface LogRepository extends JpaRepository<Log, Long>, LogRepositoryCustomQuery {

}
