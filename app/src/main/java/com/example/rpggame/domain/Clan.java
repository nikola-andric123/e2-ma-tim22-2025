package com.example.rpggame.domain;

import java.util.List;

public class Clan {
    private String uid;
    private String leaderId;
    private String name;
    private List<String> members;

    public Clan(String uid, String leaderId, String name, List<String> members) {
        this.uid = uid;
        this.leaderId = leaderId;
        this.name = name;
        this.members = members;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
