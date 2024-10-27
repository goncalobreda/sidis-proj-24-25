package com.example.authservice.usermanagement.services;

import com.example.authservice.dto.UserSyncDTO;
import com.example.authservice.exceptions.ConflictException;
import com.example.authservice.usermanagement.model.Role;
import com.example.authservice.usermanagement.model.User;
import com.example.authservice.usermanagement.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepo;
    private final EditUserMapper userEditMapper;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${server.port}")
    private String currentPort; // Porta da instância atual

    @Value("${auth.instance1.url}")
    private String authInstance1Url;

    @Value("${auth.instance2.url}")
    private String authInstance2Url;

    @Transactional
    public User create(final CreateUserRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        final User user = userEditMapper.create(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addAuthority(new Role("READER"));

        User savedUser = userRepo.save(user);

        syncUserWithOtherInstance(savedUser);

        return savedUser;
    }

    private void syncUserWithOtherInstance(User user) {
        Set<String> authoritiesAsString = user.getAuthorities().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());

        UserSyncDTO userSyncDTO = new UserSyncDTO(
                user.getUsername(),
                user.getFullName(),
                user.getPassword(),
                user.isEnabled(),
                authoritiesAsString
        );

        String otherInstanceUrl = getOtherInstanceUrl();
        logger.info("Sincronizando usuário com dados: {}", userSyncDTO);

        try {
            restTemplate.postForEntity(otherInstanceUrl + "/api/public/sync", userSyncDTO, Void.class);
        } catch (Exception e) {
            logger.error("Erro ao sincronizar com a outra instância: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao sincronizar com a outra instância: " + e.getMessage());
        }
    }




    public String getOtherInstanceUrl() {
        if (currentPort.equals("8080")) {
            return authInstance2Url;
        } else {
            return authInstance1Url;
        }
    }

    @Transactional
    public User update(final Long id, final EditUserRequest request) {
        final User user = userRepo.getById(id);
        userEditMapper.update(request, user);

        return userRepo.save(user);
    }

    @Transactional
    public User upsert(final CreateUserRequest request) {
        final Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return create(request);
        }
        final EditUserRequest updateUserRequest = new EditUserRequest(request.getFullName(), request.getAuthorities());
        return update(optionalUser.get().getId(), updateUserRequest);
    }

    @Transactional
    public User delete(final Long id) {
        final User user = userRepo.getById(id);

        // user.setUsername(user.getUsername().replace("@", String.format("_%s@",
        // user.getId().toString())));
        user.setEnabled(false);
        return userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
    }

    public boolean usernameExists(final String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public User getUser(final Long id) {
        return userRepo.getById(id);
    }

    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchUsersQuery("", "");
        }
        return userRepo.searchUsers(page, query);
    }
}
