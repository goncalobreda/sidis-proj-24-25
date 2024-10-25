package com.example.authservice.usermanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "T_USER")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    // database primary key
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    // optimistic lock concurrency control
    @Version
    private Long version;

    // auditing info
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Getter
    private LocalDateTime createdAt;

    // auditing info
    @LastModifiedDate
    @Column(nullable = false)
    @Getter
    private LocalDateTime modifiedAt;

    // auditing info
    @CreatedBy
    @Column(nullable = false, updatable = false)
    @Getter
    private String createdBy;

    // auditing info
    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    @Setter
    @Getter
    private boolean enabled = true;

    @Column(unique = true, updatable = false, nullable = false)
    @Email
    @Getter
    @NotNull
    @NotBlank
    private String username;

    @Column(nullable = false)
    @Getter
    @NotNull
    @NotBlank
    private String password;

    @Getter
    @Setter
    private String fullName;

    @ElementCollection
    @Getter
    private final Set<Role> authorities = new HashSet<>();

    protected User() {
        // for ORM only
    }

    public User(final String username, final String password) {
        this.username = username;
        setPassword(password);
    }

    public static User newUser(final String username, final String password, final String fullName) {
        final var u = new User(username, password);
        u.setFullName(fullName);
        return u;
    }

    public static User newUser(final String username, final String password, final String fullName, final String role) {
        final var u = new User(username, password);
        u.setFullName(fullName);
        u.addAuthority(new Role(role));
        return u;
    }

    public void setPassword(final String password) {
        this.password = Objects.requireNonNull(password);
    }

    public void addAuthority(final Role r) {
        authorities.add(r);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    public void applyPatch(final String username, final String password) {
        if (username != null) this.username = username;
        if (password != null) setPassword(password);
    }
}
