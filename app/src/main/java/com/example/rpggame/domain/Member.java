package com.example.rpggame.domain;

public class Member {
    private String userId;
    private String username;
    private String level;
    private String avatar;

    private String role;

    public Member(String userId, String role, String username, String level, String avatar) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.level = level;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
