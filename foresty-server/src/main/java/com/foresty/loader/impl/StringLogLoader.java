package com.foresty.loader.impl;

import com.foresty.loader.BatchLogLoader;
import com.foresty.loader.BatchLogLoaderException;
import com.foresty.loader.parser.LogMessageParser;
import com.foresty.loader.parser.impl.DefaultLogMessageParser;
import com.foresty.model.Log;
import com.foresty.repository.LogRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * Created by ericwu on 3/15/14.
 */
@Component
@Qualifier("stringLogLoader")
public class StringLogLoader implements BatchLogLoader {
    public static final String LOG_MESSAGE_SEPARATOR = "__/\n/__";

    @Autowired
    private LogRepository logRepository;

    @Override
    @Transactional
    public void loadLog(Object object, String type) throws BatchLogLoaderException {
        // TODO: right now log type is ignored. Should use it to choose the right LogMessageParser instance.
        Preconditions.checkNotNull(object);
        Preconditions.checkArgument(object instanceof String, "The log should be a string.");

        try {
            LogMessageParser logMessageParser = new DefaultLogMessageParser();
            for (String logMessage : Splitter.on(LOG_MESSAGE_SEPARATOR).trimResults().split((String) object)) {
                // ignore empty line
                if (logMessage.isEmpty()) {
                    continue;
                }

                // parse the log message
                Log log = logMessageParser.parseLogMessage(logMessage);

                // now we got the right parent of this log, save it
                this.logRepository.save(log);
            }
        } catch (DataAccessException e) {
            throw new BatchLogLoaderException(
                    "Cannot load log. Error occurred while trying to access log repository: " + e.getMessage(), e);
        }
    }
}
