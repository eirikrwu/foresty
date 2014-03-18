package com.foresty.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ericwu on 3/15/14.
 */
@Entity
public class Log {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    private Log parent;
    @Column(unique = true)
    private String name;
    private Date timestamp;
    @Column(length = 1024)
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Log getParent() {
        return parent;
    }

    public void setParent(Log parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
