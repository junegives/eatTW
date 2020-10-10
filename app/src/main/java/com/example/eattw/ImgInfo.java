package com.example.eattw;

public class ImgInfo {
    private String stringUri;
    private String description;

    public ImgInfo(String stringUri, String description){
        this.stringUri = stringUri;
        this.description = description;
    }

    public String getStringUri() {
        return stringUri;
    }

    public void setStringUri(String stringUri) {
        this.stringUri = stringUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
