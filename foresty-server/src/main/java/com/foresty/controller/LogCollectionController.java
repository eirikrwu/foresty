package com.foresty.controller;

import com.foresty.loader.BatchLogLoader;
import com.foresty.loader.BatchLogLoaderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ericwu on 3/15/14.
 */
@Controller
@RequestMapping("/log")
public class LogCollectionController {
    @Autowired
    @Qualifier("stringLogLoader")
    private BatchLogLoader batchLogLoader;

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public @ResponseBody String sendLog(@RequestBody SendLogRequest sendLogRequest) {
        try {
            this.batchLogLoader.loadLog(sendLogRequest.getLog(), sendLogRequest.getType());
            return "OK";
        } catch (BatchLogLoaderException e) {
            e.printStackTrace();
        }

        return "Error";
    }

    private static class SendLogRequest {
        private String log;
        private String type;

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
