package com.example.rpggame.domain;

public abstract class Item {
    protected String id;
    protected String name;
    protected String category;

    public Item() {} // Firestore needs empty constructor

    public Item(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

