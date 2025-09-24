package com.example.rpggame;

import com.example.rpggame.domain.UserProfile;
import com.example.rpggame.domain.Zadatak;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class LevelUpHelper {

    // Metoda vraća 'true' ako je došlo do prelaska na novi nivo, inače 'false'
    public static boolean addXpPointsAndCheckForLevelUp(UserProfile user, Zadatak zadatak) {
        int oldLevel = user.getLevel();
        int totalXpGained = 0;

        // Proveri i resetuj kvote ako je potrebno
        resetQuotasIfNeeded(user);

        // Izračunaj XP za TEŽINU, poštujući kvotu
        switch (zadatak.getTezina()) {
            case VEOMA_LAK:
                if (user.getDailyVeryEasyNormalCount() < 5) {
                    totalXpGained += getXpForDifficulty(user.getLevel(), zadatak.getTezina());
                    user.setDailyVeryEasyNormalCount(user.getDailyVeryEasyNormalCount() + 1);
                }
                break;
            case LAK:
                if (user.getDailyEasyImportantCount() < 5) {
                    totalXpGained += getXpForDifficulty(user.getLevel(), zadatak.getTezina());
                    user.setDailyEasyImportantCount(user.getDailyEasyImportantCount() + 1);
                }
                break;
            case TEZAK:
                if (user.getDailyHardExtremelyImportantCount() < 2) {
                    totalXpGained += getXpForDifficulty(user.getLevel(), zadatak.getTezina());
                    user.setDailyHardExtremelyImportantCount(user.getDailyHardExtremelyImportantCount() + 1);
                }
                break;
            case EKSTREMNO_TEZAK:
                if (user.getWeeklyExtremelyHardCount() < 1) {
                    totalXpGained += getXpForDifficulty(user.getLevel(), zadatak.getTezina());
                    user.setWeeklyExtremelyHardCount(user.getWeeklyExtremelyHardCount() + 1);
                }
                break;
        }

        // Izračunaj XP za BITNOST, poštujući kvotu
        switch (zadatak.getBitnost()) {
            case NORMALAN:
                if (user.getDailyVeryEasyNormalCount() < 5) {
                    totalXpGained += getXpForImportance(user.getLevel(), zadatak.getBitnost());
                    // Brojač se već povećao kod težine, ne treba ponovo
                }
                break;
            case VAZAN:
                if (user.getDailyEasyImportantCount() < 5) {
                    totalXpGained += getXpForImportance(user.getLevel(), zadatak.getBitnost());
                }
                break;
            case EKSTREMNO_VAZAN:
                if (user.getDailyHardExtremelyImportantCount() < 2) {
                    totalXpGained += getXpForImportance(user.getLevel(), zadatak.getBitnost());
                }
                break;
            case SPECIJALAN:
                if (user.getMonthlySpecialCount() < 1) {
                    totalXpGained += getXpForImportance(user.getLevel(), zadatak.getBitnost());
                    user.setMonthlySpecialCount(user.getMonthlySpecialCount() + 1);
                }
                break;
        }

        user.setExperiencePoints(user.getExperiencePoints() + totalXpGained);
        checkForLevelUp(user);

        return user.getLevel() > oldLevel;
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
    private static void resetQuotasIfNeeded(UserProfile user) {
        if (user.getLastQuotaReset() == null) {
            user.setLastQuotaReset(new Timestamp(new Date()));
            return;
        }

        Calendar lastReset = Calendar.getInstance();
        lastReset.setTime(user.getLastQuotaReset().toDate());
        Calendar now = Calendar.getInstance();

        // Provera da li je prošao dan
        if (lastReset.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            user.setDailyVeryEasyNormalCount(0);
            user.setDailyEasyImportantCount(0);
            user.setDailyHardExtremelyImportantCount(0);
        }

        // Provera da li je prošla nedelja
        if (lastReset.get(Calendar.WEEK_OF_YEAR) != now.get(Calendar.WEEK_OF_YEAR) || lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            user.setWeeklyExtremelyHardCount(0);
        }

        // Provera da li je prošao mesec
        if (lastReset.get(Calendar.MONTH) != now.get(Calendar.MONTH) || lastReset.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            user.setMonthlySpecialCount(0);
        }

        user.setLastQuotaReset(new Timestamp(new Date()));
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