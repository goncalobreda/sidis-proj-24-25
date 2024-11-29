package com.example.authserviceCommand.usermanagement.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NonNull
    @NotBlank
    @Email
    private String username;

    @NonNull
    @NotBlank
    private String fullName;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    private String rePassword;

    private Set<String> authorities = new HashSet<>();
    @NonNull
    @NotBlank
    private String phoneNumber;

    public CreateUserRequest(final String username, final String fullName, final String password, final String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.rePassword = password; // Definir rePassword automaticamente
        this.phoneNumber = phoneNumber; // Certifique-se de atribuir phoneNumber
    }

    public CreateUserRequest(final String username, final String fullName, final String password, final Set<String> authorities, String phoneNumber) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.rePassword = password; // Definir rePassword automaticamente
        this.authorities = authorities;
        this.phoneNumber = phoneNumber;
    }


    // Getters and Setters

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

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
