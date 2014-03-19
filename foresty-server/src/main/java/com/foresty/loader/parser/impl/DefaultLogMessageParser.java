package com.foresty.loader.parser.impl;

import com.foresty.loader.parser.LogMessageParser;
import com.foresty.model.Log;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
public class DefaultLogMessageParser implements LogMessageParser {
    public static final String PATH_PREFIX = "__";
    public static final String PATH_SAPERATOR = ",,";

    @Override
    public Log parseLogMessage(String message) {
        Log log = new Log();
        String[] pieces = message.split(PATH_PREFIX);
        if (pieces.length == 1) {
            // simply use raw message
            log.setMessage(message);
        } else {
            String pathString = pieces[pieces.length - 1];

            // get actual message
            log.setMessage(message.substring(0, message.length() - pathString.length() - PATH_PREFIX.length()));

            // get path information
            List<String> pathTokens =
                    Lists.newArrayList(Splitter.on(PATH_SAPERATOR).trimResults().omitEmptyStrings().split(pathString));
            try {
                int tokenIndex = 0;
                // first part if timestamp
                long timestamp = Long.parseLong(pathTokens.get(tokenIndex++));
                log.setTimestamp(new Date(timestamp));
                // second part is level
                int level = Integer.parseInt(pathTokens.get(tokenIndex++));
                log.setLevel(level);
                // remaining are path
                log.setPath(pathTokens.subList(tokenIndex, pathTokens.size()));
            } catch (NumberFormatException e) {
                // use raw message in case there is any error
                log.setMessage(message);
            }
        }

        return log;
    }
}
