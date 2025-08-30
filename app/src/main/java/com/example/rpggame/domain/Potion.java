package com.example.rpggame.domain;

public class Potion extends Item {
    private int strength;
    private String durability; // "oneTime" or "infinite"

    public Potion() {
        super();
        this.category = "potion";
    }

    public Potion(String id, String name, int strength, String durability) {
        super(id, name, "potion");
        this.strength = strength;
        this.durability = durability;
    }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public String getDurability() { return durability; }
    public void setDurability(String durability) { this.durability = durability; }
}

