package com.example.rpggame;

import com.example.rpggame.domain.UserProfile;

public class LevelUpHelper {

    // Metoda vraća 'true' ako je došlo do prelaska na novi nivo, inače 'false'
    public static boolean addXpPointsAndCheckForLevelUp(UserProfile user, Zadatak zadatak) {
        int oldLevel = user.getLevel();

        // Deo koda preuzet iz UserProfile.java za računanje XP-a
        int xpFromTask = calculateXpForTask(user.getLevel(), zadatak);
        user.setExperiencePoints(user.getExperiencePoints() + xpFromTask);

        // Deo koda preuzet iz UserProfile.java za proveru nivoa
        checkForLevelUp(user);

        int newLevel = user.getLevel();
        return newLevel > oldLevel;
    }

    private static int calculateXpForTask(int userLevel, Zadatak zadatak) {
        int totalXp = 0;
        totalXp += getXpForImportance(userLevel, zadatak.getBitnost());
        totalXp += getXpForDifficulty(userLevel, zadatak.getTezina());
        return totalXp;
    }

    private static int getXpForImportance(int level, Zadatak.Bitnost bitnost) {
        int baseXp;
        switch (bitnost) {
            case VAZAN: baseXp = 3; break;
            case EKSTREMNO_VAZAN: baseXp = 10; break;
            case SPECIJALAN: baseXp = 100; break;
            case NORMALAN:
            default: baseXp = 1; break;
        }
        // Formula za povećanje XP-a po nivou
        for (int i = 0; i < level; i++) {
            baseXp += Math.round(baseXp / 2.0f);
        }
        return baseXp;
    }

    private static int getXpForDifficulty(int level, Zadatak.Tezina tezina) {
        int baseXp;
        switch (tezina) {
            case LAK: baseXp = 3; break;
            case TEZAK: baseXp = 7; break;
            case EKSTREMNO_TEZAK: baseXp = 20; break;
            case VEOMA_LAK:
            default: baseXp = 1; break;
        }
        // Formula za povećanje XP-a po nivou
        for (int i = 0; i < level; i++) {
            baseXp += Math.round(baseXp / 2.0f);
        }
        return baseXp;
    }

    private static void checkForLevelUp(UserProfile user) {
        int requiredXp = 200;
        int levelToCheck = 1;

        while (true) {
            if (user.getLevel() >= levelToCheck) {
                // Ako je korisnik već na ovom ili višem nivou, izračunaj XP za sledeći
                requiredXp = (int) Math.ceil((requiredXp * 2 + requiredXp / 2.0) / 100.0) * 100;
                levelToCheck++;
                continue;
            }

            if (user.getExperiencePoints() >= requiredXp) {
                user.setLevel(user.getLevel() + 1);
                // Dodaj PP poene i novu titulu po formuli
                int newPP = 40;
                for (int i = 1; i < user.getLevel(); i++) {
                    newPP += (int) Math.round(newPP * 0.75);
                }
                user.setPowerPoints(newPP);
                // TODO: Implementirati promenu titule
            } else {
                // Nema dovoljno XP za sledeći nivo, prekidamo proveru
                break;
            }
        }
    }
}