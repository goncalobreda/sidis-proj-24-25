package com.example.readerservice.service;

import java.util.Set;

public class CreateReaderRequest {
    private String email;
    private String password;
    private String rePassword;
    private String birthdate;
    private Set<String> interests; // Adicionar esta propriedade

    // Constructor
    public CreateReaderRequest(String email, String password, String rePassword, String birthdate, Set<String> interests) {
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
        this.birthdate = birthdate;
        this.interests = interests; // Inicializar a propriedade
    }

    // Getters
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

    public Set<String> getInterests() {
        return interests; // Adicionar o getter para 'interests'
    }
}
