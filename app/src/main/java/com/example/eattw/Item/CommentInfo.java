package com.example.eattw.Item;

import java.util.Date;

public class CommentInfo {
    private String commentID;
    private String postID;
    private String userID;
    private String comment;
    private int like;
    private Date timestamp;

    public CommentInfo(String postID, String userID, String comment, Date timestamp){
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public CommentInfo(String commentID, String postID, String userID, String comment, int like, Date timestamp){
        this.commentID = commentID;
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.like = like;
        this.timestamp = timestamp;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}