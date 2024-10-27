package com.example.authservice.dto;

import com.example.authservice.usermanagement.model.Role;
import java.util.Set;

public class UserSyncDTO {
    private String username;
    private String fullName;
    private String password;
    private boolean enabled;
    private Set<String> authorities;

    // Construtor
    public UserSyncDTO(String username, String fullName, String password, boolean enabled, Set<String> authorities) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
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
}