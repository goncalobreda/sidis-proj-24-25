package com.example.readerservice.service;

import java.util.Set;

public class CreateReaderRequest {
    private String email;
    private String fullName; // Novo campo
    private String password;
    private String rePassword;
    private String birthdate;
    private Set<String> interests;
    private String phoneNumber;
    private boolean GDPR;

    // Construtor atualizado para incluir fullName, phoneNumber e GDPR
    public CreateReaderRequest(String email, String fullName, String password, String rePassword, String birthdate, Set<String> interests, String phoneNumber, boolean GDPR) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.rePassword = rePassword;
        this.birthdate = birthdate;
        this.interests = interests;
        this.phoneNumber = phoneNumber;
        this.GDPR = GDPR;
    }

    // Getters
    public String getEmail() { return email; }
    public String getFullName() { return fullName; } // Getter para fullName
    public String getPassword() { return password; }
    public String getRePassword() { return rePassword; }
    public String getBirthdate() { return birthdate; }
    public Set<String> getInterests() { return interests; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isGDPR() { return GDPR; }
}
