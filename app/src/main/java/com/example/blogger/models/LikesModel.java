package com.example.blogger.models;

public class LikesModel {
    String userId;

    public LikesModel() {
        //required
    }

    public LikesModel(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
