package com.foresty.loader.parser;

import com.foresty.model.Log;

import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public interface LogMessageParser {
    public static class LogWithPath {
        private Log log;
        private List<String> path;

        public Log getLog() {
            return log;
        }

        public void setLog(Log log) {
            this.log = log;
        }

        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }
    }

    public LogWithPath parseLogMessage(String message);
}
