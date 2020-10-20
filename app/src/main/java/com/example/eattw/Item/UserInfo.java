package com.example.eattw.Item;

public class UserInfo {
    private String userID;
    private String nickname;
    private String photoUrl;

    public UserInfo(String userID, String nickname, String photoUrl){
        this.userID = userID;
        this.nickname = nickname;
        this.photoUrl = photoUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
