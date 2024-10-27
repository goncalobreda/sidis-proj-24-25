package com.example.readerservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@EntityListeners(AuditListener.class) // Adiciona o AuditListener
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk; // database primary key

    @Version
    private long version;

    @Column(unique = false, updatable = true, nullable = false)
    @NotNull
    @NotBlank
    @Size(max=150)
    private String fullName;

    @Column(unique = false, updatable = true, nullable = false)
    @NotNull
    @NotBlank
    private String password;

    @Column(unique = true, updatable = false, nullable = false)
    private String readerID;

    @Column(unique = false, updatable = true, nullable = false)
    @Email
    @NotNull
    @NotBlank
    private String email; // serves as username

    @Column(unique = false, updatable = true, nullable = false)
    @NotNull
    @NotBlank
    private String birthdate;

    @Column(unique = false, updatable = true, nullable = false)
    @NotNull
    @NotBlank
    @Pattern(regexp = "[1-9][0-9]{8}")
    private String phoneNumber;

    @Column(unique = false, updatable = true, nullable = false)
    @NotNull
    private boolean GDPR;

    @ElementCollection
    private final Set<String> interests = new HashSet<>();

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "readerimage_id")
    private ReaderImage readerImage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    @Setter
    @Getter
    private boolean enabled = true;

    private static final long serialVersionUID = 1L;

    private static int currentYear = Year.now().getValue();
    private static int counter = 0;

    public Reader() {}

    public Reader(final String fullName, final String password, final String email, final String birthdate,
                  final String phoneNumber, final boolean GDPR) {
        this.fullName = fullName;
        setPassword(password);
        this.readerID = generateUniqueReaderID();
        this.email = email;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.GDPR = GDPR;
        this.createdAt = LocalDateTime.now();  // Atribuir o valor atual
    }

    public void initCounter(String lastReaderID) {
        if (lastReaderID != null && !lastReaderID.isBlank()) {
            String[] parts = lastReaderID.split("/");
            if (parts.length == 2) {
                currentYear = Integer.parseInt(parts[0]);
                counter = Integer.parseInt(parts[1]);
            }
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








    public String getReaderID() { return readerID; }
    public void setReaderID(String readerID) { this.readerID = readerID; }

    public String getFullName() { return fullName; }
    public void setFullName(final String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name cannot be null or blank");
        this.fullName = fullName;
    }


    public String getPassword() { return password; }
    public void setPassword(final String password) {
        this.password = Objects.requireNonNull(password);
    }

    public String getEmail() { return email; }
    public void setEmail(final String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null, nor blank");
        this.email = email;
    }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(final String birthdate) {
        if (birthdate == null) throw new IllegalArgumentException("Birthdate cannot be null");
        this.birthdate = birthdate;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(final String phoneNumber) {
        if (phoneNumber == null) throw new IllegalArgumentException("Phone number cannot be null");
        this.phoneNumber = phoneNumber;
    }

    public boolean getGDPR() { return GDPR; }
    public void setGDPR(final boolean GDPR) {
        if (!GDPR) throw new IllegalArgumentException("GDPR consent cannot be false!");
        this.GDPR = true;
    }

    public void setUniqueReaderID() {
        if (this.readerID == null) { // Gera apenas se o ID ainda não foi definido
            this.readerID = generateUniqueReaderID();
        }
    }
    public Long getVersion() { return version; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void applyPatch(final long desiredVersion, final String fullName, final String password, final String email,
                           final String birthdate, final String phoneNumber, final boolean GDPR, final Set<String> interests) {
        if (this.version != desiredVersion)
            throw new IllegalArgumentException("Object was already modified by another user");

        if (fullName != null) setFullName(fullName);
        if (password != null) setPassword(password);
        if (email != null) setEmail(email);
        if (birthdate != null) setBirthdate(birthdate);
        if (phoneNumber != null) setPhoneNumber(phoneNumber);
        setGDPR(GDPR);
        setInterests(interests);
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @PrePersist
    @PreUpdate
    public void setAuditableFields() {
        this.modifiedAt = LocalDateTime.now();
        if (this.modifiedBy == null) {
            this.modifiedBy = "system"; // ou outro valor padrão se não houver um utilizador autenticado
        }
    }

    public void addInterests(final Set<String> i) {
        interests.addAll(i);
    }

    public void setInterests(final Set<String> i) {
        interests.clear();
        interests.addAll(i);
    }

    public Set<String> getInterests() {
        return interests;
    }
}
