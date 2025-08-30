package com.example.rpggame.domain;

public class Sword extends Weapon {
    private double strengthPercentIncrease;

    public Sword() { super(); }

    public Sword(String id, String name, double strengthPercentIncrease) {
        super(id, name);
        this.strengthPercentIncrease = strengthPercentIncrease;
    }

    public double getStrengthPercentIncrease() { return strengthPercentIncrease; }
    public void setStrengthPercentIncrease(double strengthPercentIncrease) { this.strengthPercentIncrease = strengthPercentIncrease; }
}
