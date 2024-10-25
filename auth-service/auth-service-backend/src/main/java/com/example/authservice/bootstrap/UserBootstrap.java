package com.example.authservice.bootstrap;

import com.example.authservice.usermanagement.model.Role;
import com.example.authservice.usermanagement.model.User;
import com.example.authservice.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserBootstrap implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("Bootstrap running...");
        // Librarians
        if (userRepo.findByUsername("librarian1@mail.com").isEmpty()) {
            final var librarian1 = new User("librarian1@mail.com", encoder.encode("pass1"));
            librarian1.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian1);
        }
        if (userRepo.findByUsername("librarian2@mail.com").isEmpty()) {
            final var librarian2 = new User("librarian2@mail.com", encoder.encode("pass2"));
            librarian2.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian2);
        }

        if (userRepo.findByUsername("librarian3@mail.com").isEmpty()) {
            final var librarian3 = new User("librarian3@mail.com", encoder.encode("pass3"));
            librarian3.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian3);
        }

        if (userRepo.findByUsername("librarian4@mail.com").isEmpty()) {
            final var librarian4 = new User("librarian4@mail.com", encoder.encode("pass4"));
            librarian4.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian4);
        }

        if (userRepo.findByUsername("librarian5@mail.com").isEmpty()) {
            final var librarian5 = new User("librarian5@mail.com", encoder.encode("pass5"));
            librarian5.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian5);
        }

        if (userRepo.findByUsername("librarian6@mail.com").isEmpty()) {
            final var librarian6 = new User("librarian6@mail.com", encoder.encode("pass6"));
            librarian6.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian6);
        }

        if (userRepo.findByUsername("librarian7@mail.com").isEmpty()) {
            final var librarian7 = new User("librarian7@mail.com", encoder.encode("pass7"));
            librarian7.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian7);
        }

        if (userRepo.findByUsername("librarian8@mail.com").isEmpty()) {
            final var librarian8 = new User("librarian8@mail.com", encoder.encode("pass8"));
            librarian8.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian8);
        }

        if (userRepo.findByUsername("librarian9@mail.com").isEmpty()) {
            final var librarian9 = new User("librarian9@mail.com", encoder.encode("pass9"));
            librarian9.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian9);
        }

        if (userRepo.findByUsername("librarian10@mail.com").isEmpty()) {
            final var librarian10 = new User("librarian10@mail.com", encoder.encode("pass10"));
            librarian10.addAuthority(new Role(Role.LIBRARIAN));
            userRepo.save(librarian10);
        }

    }
}
