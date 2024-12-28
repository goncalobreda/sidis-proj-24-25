package com.example.acquisitionserviceQuery.dto;

import java.io.Serializable;


public class UserSyncDTO implements Serializable {
    private String username;
    private String fullName;
    private String password;
    private boolean enabled;
    private String originInstanceId;
    private String phoneNumber;

    public UserSyncDTO(String username, String fullName, String password, boolean enabled, String originInstanceId, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.enabled = enabled;
        this.originInstanceId = originInstanceId;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOriginInstanceId() {
        return originInstanceId;
    }

    public void setOriginInstanceId(String originInstanceId) {
        this.originInstanceId = originInstanceId;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}