package com.example.rpggame.domain;

public abstract class Clothes extends Item {
    public Clothes() {
        super();
        this.category = "clothes";
    }

    public Clothes(String id, String name) {
        super(id, name, "clothes");
    }
}

