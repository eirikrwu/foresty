package com.foresty.controller;

/**
 * Created by EveningSun on 14-3-23.
 */
public class Result {
    public static final Result OK = new Result(true);
    public static final Result FAIL = new Result(false);

    private boolean success;

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
