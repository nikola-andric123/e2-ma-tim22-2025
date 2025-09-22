package com.example.rpggame.domain;

public class InventoryItem {
    private String name;
    private String category;
    private Long durability;


    public InventoryItem() {}

    public String getName() { return name; }
    public String getCategory() { return category; }
    public Long getDurability() { return durability; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDurability(Long durability) {
        this.durability = durability;
    }
}
