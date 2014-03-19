package com.foresty.loader.impl;

import com.foresty.loader.BatchLogLoader;
import com.foresty.loader.BatchLogLoaderException;
import com.foresty.loader.parser.LogMessageParser;
import com.foresty.loader.parser.impl.DefaultLogMessageParser;
import com.foresty.model.Event;
import com.foresty.model.Log;
import com.foresty.repository.EventRepository;
import com.foresty.repository.LogRepository;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by ericwu on 3/15/14.
 */
@Component
@Qualifier("stringLogLoader")
public class StringLogLoader implements BatchLogLoader {
    public static final String LOG_MESSAGE_SEPARATOR = "__/\n/__";

    @Autowired
    private LogRepository logRepository;
    @Autowired
    private EventRepository eventRepository;

    @Override
    @Transactional
    public void loadLog(Object object, String type) throws BatchLogLoaderException {
        // TODO: right now log type is ignored. Should use it to choose the right LogMessageParser instance.
        Preconditions.checkNotNull(object);
        Preconditions.checkArgument(object instanceof String, "The log should be a string.");

        try {
            Map<String, Event> events = Maps.newHashMap();
            List<Log> logs = Lists.newArrayList();
            LogMessageParser logMessageParser = new DefaultLogMessageParser();
            for (String logMessage : Splitter.on(LOG_MESSAGE_SEPARATOR).trimResults().split((String) object)) {
                // ignore empty line
                if (logMessage.isEmpty()) {
                    continue;
                }

                // parse the log message
                Log log = logMessageParser.parseLogMessage(logMessage);

                // the event object in the parsed log is just a dummy container, which contains the event name and id
                // so we need to retrieve the actual event object by the event id
                // first try the memory cache
                Event event = events.get(log.getEvent().getId());
                if (event == null) {
                    // if the event object hasn't been loaded, try to load it from database
                    event = this.eventRepository.findOne(log.getEvent().getId());
                    if (event == null) {
                        // no event with this id in the database, this is the first log in this event
                        // create a new event with the dummy
                        event = log.getEvent();
                        event.setStartTime(log.getTimestamp());
                    }

                    // put the event in the memory cache
                    events.put(event.getId(), event);
                }

                // update the event
                if (event.getHighestLevel() < log.getLevel()) {
                    event.setHighestLevel(log.getLevel());
                }
                if (event.getStartTime().getTime() + event.getTimespan() < log.getTimestamp().getTime()) {
                    event.setTimespan(log.getTimestamp().getTime() - event.getStartTime().getTime());
                }

                // assign the actual event object to the log
                log.setEvent(event);

                // save the log
                logs.add(log);
            }

            // now let's save all the logs, which will cascade to the events
            this.eventRepository.save(events.values());
            this.logRepository.save(logs);
        } catch (DataAccessException e) {
            throw new BatchLogLoaderException(
                    "Cannot load log. Error occurred while trying to access log repository: " + e.getMessage(), e);
        }
    }
}
