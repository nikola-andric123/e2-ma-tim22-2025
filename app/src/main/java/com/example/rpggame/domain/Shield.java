package com.example.rpggame.domain;

public class Shield extends Clothes {
    private double hitSuccessIncrease;

    public Shield() { super(); }

    public Shield(String id, String name, double hitSuccessIncrease, String status) {
        super(id, name, status);
        this.hitSuccessIncrease = hitSuccessIncrease;
    }

    public double getHitSuccessIncrease() { return hitSuccessIncrease; }
    public void setHitSuccessIncrease(double hitSuccessIncrease) { this.hitSuccessIncrease = hitSuccessIncrease; }
}
