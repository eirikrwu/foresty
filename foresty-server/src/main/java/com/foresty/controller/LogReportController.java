package com.foresty.controller;

import com.foresty.model.Log;
import com.foresty.repository.LogRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by ericwu on 3/15/14.
 */
@Controller
@RequestMapping("/report")
public class LogReportController {
    @Autowired
    private LogRepository logRepository;

    @RequestMapping(value = "/{parentNodeDepth}/{parentNodeName}/log", method = RequestMethod.GET)
    public @ResponseBody List<Log> getLogs(@PathVariable int parentNodeDepth, @PathVariable String parentNodeName) {
        return this.logRepository.getLogs(parentNodeDepth, parentNodeName);
    }

    @RequestMapping(value = "/{parentNodeDepth}/{parentNodeName}/nodes", method = RequestMethod.GET)
    public @ResponseBody
    List<Node> getChildNodes(@PathVariable int parentNodeDepth, @PathVariable String parentNodeName) {
        List<Node> nodes = Lists.newArrayList();
        for (String nodeName : this.logRepository.getChildNodes(parentNodeDepth, parentNodeName)) {
            Node node = new Node();
            node.setNode(nodeName);
            nodes.add(node);
        }

        return nodes;
    }

    public static class Node {
        private String node;

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }
    }
}
