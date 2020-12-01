package com.example.eattw.Item;

import java.io.Serializable;
import java.util.Date;

public class CommentInfo implements Serializable {
    private String commentID;
    private String postID;
    private String userID;
    private String comment;
    private int like;
    private Date timestamp;
    private String reply_userID;
    private int depth;
    private Date bundleID;
    private boolean deleted;

    public CommentInfo(String postID, String userID, String comment, Date timestamp, String reply_userID, int depth, Date bundleID, boolean deleted){
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.timestamp = timestamp;
        this.reply_userID = reply_userID;
        this.depth = depth;
        this.bundleID = bundleID;
        this.deleted = deleted;
    }

    public CommentInfo(String commentID, String postID, String userID, String comment, int like, Date timestamp, String reply_userID, int depth, Date bundleID, boolean deleted){
        this.commentID = commentID;
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.like = like;
        this.timestamp = timestamp;
        this.reply_userID = reply_userID;
        this.depth = depth;
        this.bundleID = bundleID;
        this.deleted = deleted;
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

    public String getReply_userID() {
        return reply_userID;
    }

    public void setReply_userID(String reply_userID) {
        this.reply_userID = reply_userID;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Date getBundleID() {
        return bundleID;
    }

    public void setBundleID(Date bundleID) {
        this.bundleID = bundleID;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}