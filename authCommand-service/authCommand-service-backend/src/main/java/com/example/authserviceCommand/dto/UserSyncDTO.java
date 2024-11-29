package com.example.authserviceCommand.dto;

import java.io.Serializable;
import java.util.Set;

public class UserSyncDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String fullName;
    private String password;
    private boolean enabled;
    private Set<String> authorities;
    private String originInstanceId; // Novo campo para identificar a origem
    private String phoneNumber;

    // Construtor vazio
    public UserSyncDTO() {
        // Necessário para Jackson
    }

    // Construtor com argumentos
    public UserSyncDTO(String username, String fullName, String password, boolean enabled, Set<String> authorities, String originInstanceId, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
        this.originInstanceId = originInstanceId;
        this.phoneNumber = phoneNumber;
    }

    // Getters e Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Set<String> getAuthorities() { return authorities; }
    public void setAuthorities(Set<String> authorities) { this.authorities = authorities; }

    public String getOriginInstanceId() { return originInstanceId; }
    public void setOriginInstanceId(String originInstanceId) { this.originInstanceId = originInstanceId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}
