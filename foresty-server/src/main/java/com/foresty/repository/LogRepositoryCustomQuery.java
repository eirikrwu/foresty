package com.foresty.repository;

import com.foresty.model.Log;

import java.util.List;

/**
 * Created by EveningSun on 14-3-18.
 */
public interface LogRepositoryCustomQuery {
    /**
     * Node depth starts at 1. But here parentNodeDepth can be 0, which means the root of the entire log tree.
     *
     * @param parentNodeName
     * @param parentNodeDepth
     * @return
     */
    public List<String> getChildNodes(int parentNodeDepth, String parentNodeName);

    /**
     * Node depth starts at 1. But here parentNodeDepth can be 0, which means the root of the entire log tree.
     *
     * @param parentNodeName
     * @param parentNodeDepth
     * @return
     */
    public List<Log> getLogs(int parentNodeDepth, String parentNodeName);
}