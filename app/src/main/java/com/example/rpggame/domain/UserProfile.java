package com.example.rpggame.domain;

import com.example.rpggame.Enums.UserTitle;
import com.example.rpggame.domain.Zadatak;
import com.google.firebase.Timestamp;

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
    private String clanId;
    private Timestamp dateCreated;
    private String fcmToken; // NOVO POLJE
    private Timestamp lastQuotaReset; // Datum kada su dnevne/nedeljne/mesečne kvote poslednji put resetovane
    private int dailyVeryEasyNormalCount = 0;
    private int dailyEasyImportantCount = 0;
    private int dailyHardExtremelyImportantCount = 0;
    private int weeklyExtremelyHardCount = 0;
    private int monthlySpecialCount = 0;

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
        this.clanId = "";
        this.fcmToken = ""; // Inicijalizujemo kao prazno
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

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
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

    // NOVI GETTER I SETTER za fcmToken
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // Metoda addXpPoints ostaje nepromenjena
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
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case TEZAK:
                beginningXP = 7; // ISPRAVKA VREDNOSTI PREMA SPECIFIKACIJI
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
                            this.experiencePoints += newXp;
                            break;
                        } else{
                            currentLevel++;
                        }
                    }
                }
                break;
            case EKSTREMNO_TEZAK:
                beginningXP = 20; // ISPRAVKA VREDNOSTI PREMA SPECIFIKACIJI
                if(this.level == 0){
                    this.experiencePoints += beginningXP;

                }else {
                    while (true) {
                        if (this.level == currentLevel) {
                            newXp = beginningXP;
                            for(int x=1; x<=currentLevel;x++){
                                newXp = newXp + (int) Math.ceil(0.5 * newXp);
                            }
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
            int requiredXP = 200;
            int currentPP = 40;
            for(int i=1;i<=this.level;i++){
                requiredXP = (requiredXP * 2) + (requiredXP / 2);
                requiredXP = (int) (Math.ceil(requiredXP / 100.0) * 100);
                currentPP += (int) (currentPP * 0.75);
            }

            if(this.experiencePoints >= requiredXP){
                this.level += 1;
                this.powerPoints += currentPP;
                if(this.level == 2){
                    this.title = UserTitle.SENIOR;
                } else if (this.level == 3) {
                    this.title = UserTitle.EXPERT;
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
    public Timestamp getLastQuotaReset() { return lastQuotaReset; }
    public void setLastQuotaReset(Timestamp lastQuotaReset) { this.lastQuotaReset = lastQuotaReset; }
    public int getDailyVeryEasyNormalCount() { return dailyVeryEasyNormalCount; }
    public void setDailyVeryEasyNormalCount(int dailyVeryEasyNormalCount) { this.dailyVeryEasyNormalCount = dailyVeryEasyNormalCount; }
    public int getDailyEasyImportantCount() { return dailyEasyImportantCount; }
    public void setDailyEasyImportantCount(int dailyEasyImportantCount) { this.dailyEasyImportantCount = dailyEasyImportantCount; }
    public int getDailyHardExtremelyImportantCount() { return dailyHardExtremelyImportantCount; }
    public void setDailyHardExtremelyImportantCount(int dailyHardExtremelyImportantCount) { this.dailyHardExtremelyImportantCount = dailyHardExtremelyImportantCount; }
    public int getWeeklyExtremelyHardCount() { return weeklyExtremelyHardCount; }
    public void setWeeklyExtremelyHardCount(int weeklyExtremelyHardCount) { this.weeklyExtremelyHardCount = weeklyExtremelyHardCount; }
    public int getMonthlySpecialCount() { return monthlySpecialCount; }
    public void setMonthlySpecialCount(int monthlySpecialCount) { this.monthlySpecialCount = monthlySpecialCount; }
}