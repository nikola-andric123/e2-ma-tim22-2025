package com.example.rpggame.domain;

public class Sword extends Weapon {
    private double strengthPercentIncrease;
    private String status;

    public Sword() { super(); }

    public Sword(String id, String name, double strengthPercentIncrease, String status) {
        super(id, name, status);
        this.strengthPercentIncrease = strengthPercentIncrease;
        this.status = status;
    }

    public double getStrengthPercentIncrease() { return strengthPercentIncrease; }
    public void setStrengthPercentIncrease(double strengthPercentIncrease) { this.strengthPercentIncrease = strengthPercentIncrease; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
