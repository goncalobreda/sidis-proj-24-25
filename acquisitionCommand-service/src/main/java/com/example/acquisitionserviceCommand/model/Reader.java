package com.example.acquisitionserviceCommand.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

@Entity
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "reader_id", unique = true, nullable = false)
    private String readerId;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean enabled;

    @Version
    private Long version;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Column
    private String birthdate;

    private static int currentYear = Year.now().getValue();
    private static int counter = 0;

    public Reader() {
    }

    public Reader(String fullName, String email, LocalDateTime createdAt, boolean enabled) {
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
        this.enabled = enabled;
        this.readerId = generateUniqueReaderID();
    }

    public static void initCounter(String lastReaderId) {
        if (lastReaderId != null && !lastReaderId.isBlank()) {
            String[] parts = lastReaderId.split("/");
            if (parts.length == 2) {
                currentYear = Integer.parseInt(parts[0]);
                counter = Integer.parseInt(parts[1]);
            }
        } else {
            currentYear = Year.now().getValue();
            counter = 0;
        }
    }

    private synchronized String generateUniqueReaderID() {
        if (Year.now().getValue() != currentYear) {
            currentYear = Year.now().getValue();
            counter = 0;
        }
        counter++;
        return currentYear + "/" + counter;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.readerId == null || this.readerId.isBlank()) {
            this.readerId = generateUniqueReaderID();
        }
    }

    // Getters e Setters
    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setUniqueReaderID() {
        if (this.readerId == null) { // Gera apenas se o ID ainda não foi definido
            this.readerId = generateUniqueReaderID();
        }
    }
}
