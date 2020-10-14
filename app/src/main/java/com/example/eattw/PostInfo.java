package com.example.eattw;

import java.util.ArrayList;
import java.util.Date;

public class PostInfo {
    private String category;
    private String title;
    private String content;
    private String publisher;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<String> desList = new ArrayList<String>();
    //날짜
    private Date createdAt;
    //좋아요
    private int like;
    //스크랩
    private int scrap;
    //댓글
    private int comments;

    public PostInfo(String category, String title, String content, String publisher, ArrayList<String> imageList, ArrayList<String> desList, Date createdAt){
        this.category = category;
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.imageList = imageList;
        this.desList = desList;
        this.createdAt = createdAt;
//        this.img = img;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
