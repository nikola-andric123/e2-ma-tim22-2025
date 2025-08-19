package com.example.rpggame.domain;

import com.example.rpggame.Enums.UserTitle;
import com.example.rpggame.Zadatak;
import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.sql.Time;

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
    private Timestamp dateCreated;

    public UserProfile(){

    }
    public UserProfile(String username, String email, int level, int powerPoints, int experiencePoints, int collectedCoins, int numberOfBadges, UserTitle title, String avatar,
                       Timestamp dateCreated) {
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

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
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

    public void addXpPoints(Zadatak zadatak){
        int currentLevel = 1;
        int beginningXP;
        int newXp = 0;
        switch (zadatak.getBitnost()){

            case NORMALAN:

                beginningXP = 1;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case VAZAN:
                beginningXP = 3;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case EKSTREMNO_VAZAN:
                beginningXP = 10;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case SPECIJALAN:
                beginningXP = 100;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
        }
        newXp = 0;
        currentLevel = 1;
        switch (zadatak.getTezina()){

            case VEOMA_LAK:

                beginningXP = 1;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case LAK:
                beginningXP = 3;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case TEZAK:
                beginningXP = 10;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case EKSTREMNO_TEZAK:
                beginningXP = 100;
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            //int previous = (int) Math.ceil((2.0/3.0) * (currentLevel+1));
                            //this.experiencePoints += previous + (int) Math.ceil(0.5*previous);
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
        }
        //Check for level up
        if(this.level == 0){
            if(this.experiencePoints >= 200) {
                this.level = 1;
                this.powerPoints += 40;
                this.title = UserTitle.INTERMEDIATE;
            }
        }else{
            int currentPoints = 500;
            int currentPP = 70;
            while(true){
                if(this.level == currentLevel){
                    if(this.experiencePoints >= currentPoints){
                        this.level += 1;
                        this.powerPoints += currentPP;
                        if(this.level == 2){
                            this.title = UserTitle.SENIOR;
                        } else if (this.level == 3) {
                            this.title = UserTitle.EXPERT;
                        }
                        break;
                    }else{
                        break;
                    }
                }else{
                    currentLevel++;
                    currentPoints += (int) (Math.ceil((5.0/2.0) * (double) currentPoints/100.0) * 100);
                    currentPP += (int) ((7.0/4.0) * currentPP);
                }
            }
        }

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
