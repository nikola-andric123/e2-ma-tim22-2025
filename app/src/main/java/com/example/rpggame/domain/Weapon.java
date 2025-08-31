package com.example.rpggame.domain;

public abstract class Weapon extends Item {
    public Weapon() {
        super();
        this.category = "weapon";
    }

    public Weapon(String id, String name, String status) {
        super(id, name, "weapon", status);
    }
}

