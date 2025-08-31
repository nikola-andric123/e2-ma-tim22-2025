package com.example.rpggame.domain;

public class Boots extends Clothes {
    private double attackChanceIncrease;


    public Boots() { super(); }

    public Boots(String id, String name, double attackChanceIncrease, String status) {
        super(id, name, status);
        this.attackChanceIncrease = attackChanceIncrease;
        this.status = status;
    }

    public double getAttackChanceIncrease() { return attackChanceIncrease; }
    public void setAttackChanceIncrease(double attackChanceIncrease) { this.attackChanceIncrease = attackChanceIncrease; }


}