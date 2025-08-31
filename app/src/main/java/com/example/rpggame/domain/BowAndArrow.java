package com.example.rpggame.domain;

public class BowAndArrow extends Weapon {
    private double coinsPercentIncrease;


    public BowAndArrow() { super(); }

    public BowAndArrow(String id, String name, double coinsPercentIncrease, String status) {
        super(id, name, status);
        this.coinsPercentIncrease = coinsPercentIncrease;
        this.status = status;
    }

    public double getCoinsPercentIncrease() { return coinsPercentIncrease; }
    public void setCoinsPercentIncrease(double coinsPercentIncrease) { this.coinsPercentIncrease = coinsPercentIncrease; }


}
