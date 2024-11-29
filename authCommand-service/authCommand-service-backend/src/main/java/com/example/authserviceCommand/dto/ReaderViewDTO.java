package com.example.authserviceCommand.dto;

import java.util.Set;

public class ReaderViewDTO {
    private String name;
    private String readerID;
    private String email;
    private String birthdate;
    private String phoneNumber;
    private Set<String> interests;
    private String imageUrl;

    // Constructor
    public ReaderViewDTO(String name, String readerID, String email, String birthdate, String phoneNumber, Set<String> interests, String imageUrl) {
        this.name = name;
        this.readerID = readerID;
        this.email = email;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.interests = interests;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getReaderID() { return readerID; }
    public String getEmail() { return email; }
    public String getBirthdate() { return birthdate; }
    public String getPhoneNumber() { return phoneNumber; }
    public Set<String> getInterests() { return interests; }
    public String getImageUrl() { return imageUrl; }
}
