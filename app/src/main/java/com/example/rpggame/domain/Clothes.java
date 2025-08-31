package com.example.rpggame.domain;

public abstract class Clothes extends Item {


    public Clothes() {
        super();
        this.category = "clothes";
    }

    public Clothes(String id, String name, String status) {
        super(id, name, "clothes",status);
        this.status = status;
    }
}

