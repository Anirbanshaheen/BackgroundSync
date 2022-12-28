package com.example.backgroundsync.model;

public class PostModel {
    public int userId;
    public int it;
    public String title;
    public String body;

    public PostModel() {
    }

    public PostModel(int userId, int it, String title, String body) {
        this.userId = userId;
        this.it = it;
        this.title = title;
        this.body = body;
    }
}
