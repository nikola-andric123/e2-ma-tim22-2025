package com.example.rpggame.domain;

import com.google.firebase.Timestamp; // NOVI IMPORT
import java.util.List;

public class Clan {
    private String uid;
    private String leaderId;
    private String name;
    private List<String> members;
    private Timestamp createdAt; // NOVO POLJE

    public Clan() {}

    // Getteri i Setteri...

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    // NOVI GETTER I SETTER
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}