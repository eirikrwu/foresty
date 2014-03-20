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
    private Date timestamp;
    private int level;
    @Column(columnDefinition = "TEXT")
    private String message;
    @ManyToOne
    private Event event;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
