package com.foresty;

import com.foresty.client.appender.ForestyLog4j;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Random;

/**
 * Created by ericwu on 3/16/14.
 */
public class ForestyRestfulAppenderDemo {
    private static final Logger LOGGER = Logger.getLogger(ForestyRestfulAppenderDemo.class);
    private static final Random RANDOM = new Random(new Date().getTime());

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override public void run() {
                while (true) {
                    ForestyLog4j.beginEvent("event1");
                    LOGGER.info("log message 1");
                    LOGGER.info("log message 2");
                    LOGGER.info("log message 3");
                    ForestyLog4j.exitEvent();

                    try {
                        Thread.sleep(500 + RANDOM.nextInt(3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override public void run() {
                while (true) {
                    ForestyLog4j.beginEvent("event2");
                    LOGGER.info("log message 4");
                    LOGGER.info("log message 5");
                    LOGGER.info("log message 6");
                    ForestyLog4j.exitEvent();

                    try {
                        Thread.sleep(500 + RANDOM.nextInt(3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
