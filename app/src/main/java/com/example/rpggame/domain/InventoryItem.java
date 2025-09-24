package com.example.rpggame.domain;

public class InventoryItem {
    private String name;
    private String category;
    private Long durability;
    private Double powerIncreasePercent;
    private Double coinsIncreasePercent;


    public InventoryItem() {}

    public String getName() { return name; }
    public String getCategory() { return category; }

    public Double getPowerIncreasePercent() {
        return powerIncreasePercent;
    }

    public void setPowerIncreasePercent(Double powerIncreasePercent) {
        this.powerIncreasePercent = powerIncreasePercent;
    }

    public Double getCoinsIncreasePercent() {
        return coinsIncreasePercent;
    }

    public void setCoinsIncreasePercent(Double coinsIncreasePercent) {
        this.coinsIncreasePercent = coinsIncreasePercent;
    }

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
