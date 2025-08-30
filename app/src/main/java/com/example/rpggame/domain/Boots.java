package com.example.rpggame.domain;

public class Boots extends Clothes {
    private double attackChanceIncrease;

    public Boots() { super(); }

    public Boots(String id, String name, double attackChanceIncrease) {
        super(id, name);
        this.attackChanceIncrease = attackChanceIncrease;
    }

    public double getAttackChanceIncrease() { return attackChanceIncrease; }
    public void setAttackChanceIncrease(double attackChanceIncrease) { this.attackChanceIncrease = attackChanceIncrease; }
}