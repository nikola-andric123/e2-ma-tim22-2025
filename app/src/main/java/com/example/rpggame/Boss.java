package com.example.rpggame;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "boss_table")
public class Boss {

    @PrimaryKey
    @NonNull
    private int level;

    private int maxHp;
    private boolean isDefeated;

    public Boss() {}

    // ISPRAVKA: Dodata @Ignore anotacija
    @Ignore
    public Boss(int level, int maxHp, boolean isDefeated) {
        this.level = level;
        this.maxHp = maxHp;
        this.isDefeated = isDefeated;
    }

    // Getteri i Setteri...
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public boolean isDefeated() { return isDefeated; }
    public void setDefeated(boolean defeated) { isDefeated = defeated; }
}