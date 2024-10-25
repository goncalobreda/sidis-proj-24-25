package com.example.authservice.dto;

import java.util.Set;

public class CreateReaderRequestDTO {
    private String email;
    private String password;
    private String rePassword;
    private String birthdate;
    private Set<String> interests;

    // Constructor
    public CreateReaderRequestDTO(String email, String password, String rePassword, String birthdate, Set<String> interests) {
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
        this.birthdate = birthdate;
        this.interests = interests;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRePassword() { return rePassword; }
    public String getBirthdate() { return birthdate; }
    public Set<String> getInterests() { return interests; }
}
