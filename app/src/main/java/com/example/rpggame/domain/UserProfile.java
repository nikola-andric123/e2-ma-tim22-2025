package com.example.rpggame.domain;

import com.example.rpggame.Enums.UserTitle;
import com.google.type.DateTime;

public class UserProfile {

    private String username;
    private String email;
    private int level;
    private int powerPoints;
    private int experiencePoints;
    private int collectedCoins;
    private int numberOfBadges;
    private UserTitle title;
    private String avatar;
    private DateTime dateCreated;

    public UserProfile(){

    }
    public UserProfile(String username, String email, int level, int powerPoints, int experiencePoints, int collectedCoins, int numberOfBadges, UserTitle title, String avatar,
                       DateTime dateCreated) {
        this.username = username;
        this.email = email;
        this.level = level;
        this.powerPoints = powerPoints;
        this.experiencePoints = experiencePoints;
        this.collectedCoins = collectedCoins;
        this.numberOfBadges = numberOfBadges;
        this.title = title;
        this.avatar = avatar;
        this.dateCreated = dateCreated;
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPowerPoints() {
        return powerPoints;
    }

    public void setPowerPoints(int powerPoints) {
        this.powerPoints = powerPoints;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }

    public void setCollectedCoins(int collectedCoins) {
        this.collectedCoins = collectedCoins;
    }

    public int getNumberOfBadges() {
        return numberOfBadges;
    }

    public void setNumberOfBadges(int numberOfBadges) {
        this.numberOfBadges = numberOfBadges;
    }

    public UserTitle getTitle() {
        return title;
    }

    public void setTitle(UserTitle title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
