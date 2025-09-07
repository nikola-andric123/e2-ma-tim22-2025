package com.example.rpggame.domain;

public class Friend {
    private String uid;
    private String username;
    private String profileImageUrl;
    private int level;

    public Friend() {} // Needed for Firestore

    public Friend(String uid, String username, String profileImageUrl, int level) {
        this.uid = uid;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.level = level;
    }

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public int getLevel() { return level; }
}
