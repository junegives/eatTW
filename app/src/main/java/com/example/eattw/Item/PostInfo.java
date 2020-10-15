package com.example.eattw.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostInfo {
    private String postID;
    private String userID;
    private String category;
    private String title;
    private String content;
    private String nickname;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<String> desList = new ArrayList<String>();
    private Date timestamp;
    private int like;
    private int scrap;
    private int comments;

    //처음 등록할 때
    public PostInfo(String userID, String category, String title, String content, String nickname, ArrayList<String> imageList, ArrayList<String> desList, Date timestamp, int like, int scrap, int comments) {
        this.userID = userID;
        this.category = category;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.imageList = imageList;
        this.desList = desList;
        this.timestamp = timestamp;
        this.like = like;
        this.scrap = scrap;
        this.comments = comments;
    }

    //다음 가져올 때
    public PostInfo(String postID, String userID, String category, String title, String content, String nickname, ArrayList<String> imageList, ArrayList<String> desList, Date timestamp, int like, int scrap, int comments) {
        this.postID = postID;
        this.userID = userID;
        this.category = category;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.imageList = imageList;
        this.desList = desList;
        this.timestamp = timestamp;
        this.like = like;
        this.scrap = scrap;
        this.comments = comments;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<String> getDesList() {
        return desList;
    }

    public void setDesList(ArrayList<String> desList) {
        this.desList = desList;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getScrap() {
        return scrap;
    }

    public void setScrap(int scrap) {
        this.scrap = scrap;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
