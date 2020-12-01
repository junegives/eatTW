package com.example.eattw.Item;

import java.io.Serializable;
import java.util.Date;

public class QueryItem implements Serializable {
    private String title;
    private int comments;
    private Date timestamp;

    public QueryItem(String title, int comments, Date timestamp) {
        this.title = title;
        this.comments = comments;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
