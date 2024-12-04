package com.example.bookservice.messaging;

import java.io.Serializable;

public class AuthorEventMessage implements Serializable {
    private String action;
    private String authorID;
    private String name;
    private String biography;

    public AuthorEventMessage(String action, String authorID, String name, String biography) {
        this.action = action;
        this.authorID = authorID;
        this.name = name;
        this.biography = biography;
    }

    // Getters e Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        return "AuthorEventMessage{" +
                "action='" + action + '\'' +
                ", authorID='" + authorID + '\'' +
                ", name='" + name + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }
}
