package com.example.authserviceQuery.bootstrap;

import com.example.authserviceQuery.dto.UserSyncDTO;
import com.example.authserviceQuery.usermanagement.model.Role;
import com.example.authserviceQuery.usermanagement.model.User;
import com.example.authserviceQuery.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserBootstrap implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${instance.id}")
    private String instanceId;

    @Override
    public void run(final String... args) {
        System.out.println("Bootstrap running for instance: " + instanceId);

        createAndSyncUser("librarian1@mail.com", "pass1");
        createAndSyncUser("librarian2@mail.com", "pass2");
    }

    private void createAndSyncUser(String email, String password) {
        if (userRepo.findByUsername(email).isEmpty()) {
            User user = new User(email, encoder.encode(password));
            user.addAuthority(new Role(Role.LIBRARIAN));
            User savedUser = userRepo.save(user);

            UserSyncDTO userSyncDTO = new UserSyncDTO(
                    savedUser.getUsername(),
                    savedUser.getFullName(),
                    savedUser.getPassword(),
                    savedUser.isEnabled(),
                    savedUser.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toSet()),
                    instanceId,
                    savedUser.getPhoneNumber()
            );
        }
    }

}
