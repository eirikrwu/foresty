package com.foresty.controller;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by EveningSun on 14-3-28.
 */
public class PagedResults<T> {
    private List<T> records;
    private int queryRecordCount;
    private long totalRecordCount;

    public PagedResults() {
    }

    public PagedResults(Page<T> page) {
        this.records = page.getContent();
        this.queryRecordCount = page.getNumberOfElements();
        this.totalRecordCount = page.getTotalElements();
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getQueryRecordCount() {
        return queryRecordCount;
    }

    public void setQueryRecordCount(int queryRecordCount) {
        this.queryRecordCount = queryRecordCount;
    }

    public long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
