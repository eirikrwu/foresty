package com.foresty.controller;

import com.foresty.model.Log;
import com.foresty.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
@Controller
@RequestMapping("/log")
public class LogReportController {
    @Autowired
    private LogRepository logRepository;

    @RequestMapping(value = "/{parentId}", method = RequestMethod.GET)
    public @ResponseBody List<Log> getLogsByParentId(@PathVariable long parentId) {
        return this.logRepository.getLogsByParentId(parentId);
    }
}
