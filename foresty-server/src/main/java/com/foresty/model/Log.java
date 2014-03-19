package com.foresty.model;

import com.google.common.collect.Lists;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * Created by ericwu on 3/15/14.
 */
@Entity
public class Log {
    public static final int MAX_DEPTH = 5;

    @Id
    @GeneratedValue
    private long id;
    private Date timestamp;
    private int level;
    @Column(length = 1024)
    private String message;

    private String node1;
    private String node2;
    private String node3;
    private String node4;
    private String node5;

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

    public String getNode1() {
        return node1;
    }

    public void setNode1(String node1) {
        this.node1 = node1;
    }

    public String getNode2() {
        return node2;
    }

    public void setNode2(String node2) {
        this.node2 = node2;
    }

    public String getNode3() {
        return node3;
    }

    public void setNode3(String node3) {
        this.node3 = node3;
    }

    public String getNode4() {
        return node4;
    }

    public void setNode4(String node4) {
        this.node4 = node4;
    }

    public String getNode5() {
        return node5;
    }

    public void setNode5(String node5) {
        this.node5 = node5;
    }

    public List<String> getPath() {
        List<String> path = Lists.newArrayList();
        if (this.node1 != null) {
            path.add(this.node1);
            if (this.node2 != null) {
                path.add(this.node2);
                if (this.node3 != null) {
                    path.add(this.node3);
                    if (this.node4 != null) {
                        path.add(this.node4);
                        if (this.node5 != null) {
                            path.add(this.node5);
                        }
                    }
                }
            }
        }

        return path;
    }

    public void setPath(List<String> path) {
        int pathLength = path.size();
        if (pathLength > 0) {
            this.setNode1(path.get(0));
            if (pathLength > 1) {
                this.setNode2(path.get(1));
                if (pathLength > 2) {
                    this.setNode3(path.get(2));
                    if (pathLength > 3) {
                        this.setNode4(path.get(3));
                        if (pathLength > 4) {
                            this.setNode5(path.get(4));
                        }
                    }
                }
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
