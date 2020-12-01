package com.example.eattw.Item;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostInfo implements Serializable {
    private String postID;
    private String category;
    private String title;
    private String content;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<String> desList = new ArrayList<String>();
    private Date timestamp;
    private int like;
    private int scrap;
    private int comments;
    private DocumentReference userRef;

    //처음 등록할 때
    public PostInfo(String category, String title, String content, ArrayList<String> imageList, ArrayList<String> desList, Date timestamp, int like, int scrap, int comments, DocumentReference userRef) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageList = imageList;
        this.desList = desList;
        this.timestamp = timestamp;
        this.like = like;
        this.scrap = scrap;
        this.comments = comments;
        this.userRef = userRef;
    }

    //목록에 필요한 정보 가져올 때
    public PostInfo(String postID, String title, String content, ArrayList<String> imageList, Date timestamp, int like, int scrap, int comments, DocumentReference userRef){
        this.postID = postID;
        this.title = title;
        this.content = content;
        this.imageList = imageList;
        this.timestamp = timestamp;
        this.like = like;
        this.scrap = scrap;
        this.comments = comments;
        this.userRef = userRef;
    }

    //게시글 정보 전체 가져올 때
    public PostInfo(String postID, String category, String title, String content, ArrayList<String> imageList, ArrayList<String> desList, Date timestamp, int like, int scrap, int comments, DocumentReference userRef) {
        this.postID = postID;
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageList = imageList;
        this.desList = desList;
        this.timestamp = timestamp;
        this.like = like;
        this.scrap = scrap;
        this.comments = comments;
        this.userRef = userRef;
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

    public DocumentReference getUserRef() {
        return userRef;
    }

    public void setUserRef(DocumentReference userRef) {
        this.userRef = userRef;
    }
}
