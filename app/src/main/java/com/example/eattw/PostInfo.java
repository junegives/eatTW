package com.example.eattw;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class PostInfo {
    private String category;
    private String title;
    private String content;
    private String publisher;
    HashMap<String, String> img = new HashMap<String, String>();

    public PostInfo(String category, String title, String content, String publisher, HashMap<String, String> img){
        this.category = category;
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.img = img;
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

    public HashMap<String, String> getImg() {
        return img;
    }

    public void setImg(HashMap<String, String> img) {
        this.img = img;
    }
}
