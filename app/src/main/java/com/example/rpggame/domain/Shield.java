package com.example.rpggame.domain;

public class Shield extends Clothes {
    private double hitSuccessIncrease;

    public Shield() { super(); }

    public Shield(String id, String name, double hitSuccessIncrease) {
        super(id, name);
        this.hitSuccessIncrease = hitSuccessIncrease;
    }

    public double getHitSuccessIncrease() { return hitSuccessIncrease; }
    public void setHitSuccessIncrease(double hitSuccessIncrease) { this.hitSuccessIncrease = hitSuccessIncrease; }
}
