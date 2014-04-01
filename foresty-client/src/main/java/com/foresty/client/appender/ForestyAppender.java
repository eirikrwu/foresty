package com.foresty.client.appender;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPOutputStream;

/**
 * Created by ericwu on 3/15/14.
 */
public class ForestyAppender extends AppenderSkeleton {
    public static final String LOG_MESSAGE_SEPARATOR = "__/\n/__";
    public static final String PATH_PREFIX = "__begin-foresty__";
    public static final String PATH_SAPERATOR = ",,";

    public static final String EVENT_KEY = "__forestyEvent";
    public static final String EVENT_ID_KEY = "__forestyEventId";

    public static final String DEFAULT_EVENT_NAME = "__ungroupedLog";
    public static final String DEFAULT_EVENT_ID = "__defaultUngroupedEventId";

    private final LogSendingThread logSendingThread;

    private String forestyUrl;
    private int flushInterval = 10;
    private boolean eventLogOnly = true;

    public ForestyAppender() {
        this.logSendingThread = new LogSendingThread();
        this.logSendingThread.setDaemon(true);
        this.logSendingThread.start();
    }

    public class LogSendingThread extends Thread {
        private final Queue<String> cachedLoggings = new ConcurrentLinkedQueue<String>();

        public void addLog(String log) {
            this.cachedLoggings.add(log);
        }

        public synchronized void flush() {
            // take a snapshot of the loggings
            int size = this.cachedLoggings.size();
            // do nothing if the snapshot is empty
            if (size == 0) {
                return;
            }
            // snapshot
            List<String> loggings = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                String log = this.cachedLoggings.remove();
                loggings.add(log);
            }
            String logs = Joiner.on(LOG_MESSAGE_SEPARATOR).join(loggings);

            Map<String, String> logRequest = Maps.newHashMap();
            logRequest.put("log", logs);
            logRequest.put("type", "default");

            HttpURLConnection connection = null;
            try {
                String urlString = forestyUrl + (forestyUrl.endsWith("/") ? "q/log" : "/q/log");
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Content-Encoding", "gzip");
                connection.setDoOutput(true);

                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(connection.getOutputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(gzipOutputStream);
                try {
                    dataOutputStream.writeBytes(new ObjectMapper().writeValueAsString(logRequest));
                    dataOutputStream.flush();
                    gzipOutputStream.flush();
                } finally {
                    try {
                        dataOutputStream.close();
                    } finally {
                        gzipOutputStream.close();
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    System.err.println("[" + new Date() +
                            "] Error occurred while sending log message to foresty server. Returned status code is " +
                            responseCode + ". Some log messages have been dropped silently.");

                    System.err.println("What we sent is: " + logRequest);

                    // read error page
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    try {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            System.err.println(inputLine);
                        }
                    } finally {
                        in.close();
                    }
                }
            } catch (IOException e) {
                //FIXME: handle error
                System.err.println("IOError occurred while sending log message to foresty server: " + e.getMessage());
                System.err.println("Because of the error, some log messages have been dropped silently.");
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(flushInterval * 1000);
                } catch (InterruptedException e) {
                    // ignore
                    e.printStackTrace();
                }

                flush();
            }
        }
    }

    @Override
    protected void append(LoggingEvent event) {
        // check foresty event first
        String eventName = (String) event.getMDC(EVENT_KEY);
        String eventId = (String) event.getMDC(EVENT_ID_KEY);
        if (eventName == null && !this.eventLogOnly) {
            eventName = DEFAULT_EVENT_NAME;
            eventId = DEFAULT_EVENT_ID;
        }

        // if there is no event and this appender is event log only, ignore the log message
        if (eventName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLayout().format(event).trim());
            sb.append(PATH_PREFIX);

            // first element is timestamp
            sb.append(event.getTimeStamp());
            sb.append(PATH_SAPERATOR);
            // send element level
            sb.append(event.getLevel().toInt());
            sb.append(PATH_SAPERATOR);
            // event name
            sb.append(eventName);
            sb.append(PATH_SAPERATOR);
            // event id
            sb.append(eventId);
            sb.append(PATH_SAPERATOR);

            // add event string to cache
            this.logSendingThread.addLog(sb.toString());
        }
    }

    @Override
    public void close() {
        this.logSendingThread.flush();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    public String getForestyUrl() {
        return forestyUrl;
    }

    public void setForestyUrl(String forestyUrl) {
        this.forestyUrl = forestyUrl;
    }

    public boolean isEventLogOnly() {
        return eventLogOnly;
    }

    public void setEventLogOnly(boolean eventLogOnly) {
        this.eventLogOnly = eventLogOnly;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }
}
