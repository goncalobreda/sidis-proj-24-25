package com.example.readerservice.service;

import jakarta.validation.constraints.Pattern;

import java.util.Set;

public class EditReaderRequest {
    private String name;
    private String email;
    private String password;
    private String rePassword;
    private String birthdate;

    @Pattern(regexp = "[1-9][0-9]{8}")
    private String phoneNumber;
    private boolean GDPR;
    private Set<String> interests;

    // Constructor
    public EditReaderRequest(String name, String email, String password, String rePassword,
                             String birthdate, String phoneNumber, boolean GDPR, Set<String> interests) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.GDPR = GDPR;
        this.interests = interests;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isGDPR() {
        return GDPR;
    }

    public Set<String> getInterests() {
        return interests;
    }
}
