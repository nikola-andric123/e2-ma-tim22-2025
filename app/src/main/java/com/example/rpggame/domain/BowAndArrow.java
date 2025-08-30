package com.example.rpggame.domain;

public class BowAndArrow extends Weapon {
    private double coinsPercentIncrease;

    public BowAndArrow() { super(); }

    public BowAndArrow(String id, String name, double coinsPercentIncrease) {
        super(id, name);
        this.coinsPercentIncrease = coinsPercentIncrease;
    }

    public double getCoinsPercentIncrease() { return coinsPercentIncrease; }
    public void setCoinsPercentIncrease(double coinsPercentIncrease) { this.coinsPercentIncrease = coinsPercentIncrease; }
}
