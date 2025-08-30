package com.example.rpggame.domain;

public class Gloves extends Clothes {
    private int powerIncrease;

    public Gloves() { super(); }

    public Gloves(String id, String name, int powerIncrease) {
        super(id, name);
        this.powerIncrease = powerIncrease;
    }

    public int getPowerIncrease() { return powerIncrease; }
    public void setPowerIncrease(int powerIncrease) { this.powerIncrease = powerIncrease; }
}
