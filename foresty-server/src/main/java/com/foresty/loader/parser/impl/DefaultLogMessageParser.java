package com.foresty.loader.parser.impl;

import com.foresty.loader.parser.LogMessageParser;
import com.foresty.model.Event;
import com.foresty.model.Log;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public class DefaultLogMessageParser implements LogMessageParser {
    public static final String PATH_PREFIX = "__begin-foresty__";
    public static final String PATH_SAPERATOR = ",,";

    @Override
    public Log parseLogMessage(String message) {
        Log log = new Log();
        String[] pieces = message.split(PATH_PREFIX);
        if (pieces.length == 1) {
            // simply use raw message
            // FIXME: should never reach here
            log.setMessage(message);
        } else {
            String pathString = pieces[pieces.length - 1];

            // get actual message
            // FIXME: deal with message trancation
            log.setMessage(message.substring(0,
                    Math.min(1023, message.length() - pathString.length() - PATH_PREFIX.length())));

            // get path information
            List<String> pathTokens =
                    Lists.newArrayList(Splitter.on(PATH_SAPERATOR).trimResults().omitEmptyStrings().split(pathString));
            try {
                int tokenIndex = 0;
                // get timestamp
                long timestamp = Long.parseLong(pathTokens.get(tokenIndex++));
                log.setTimestamp(new Date(timestamp));
                // get level
                int level = Integer.parseInt(pathTokens.get(tokenIndex++));
                log.setLevel(level);

                // create event
                Event event = new Event();
                log.setEvent(event);
                // get event name
                String eventName = pathTokens.get(tokenIndex++);
                event.setName(eventName);
                // get event id
                String eventId = pathTokens.get(tokenIndex++);
                event.setId(eventId);
            } catch (NumberFormatException e) {
                // use raw message in case there is any error
                // FIXME: deal with error properly, this will leave null log.event
                e.printStackTrace();
                System.out.println(message);
                log.setMessage(message);
            }
        }

        return log;
    }
}
