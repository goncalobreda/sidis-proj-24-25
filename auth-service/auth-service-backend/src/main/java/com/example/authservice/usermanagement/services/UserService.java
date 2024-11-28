package com.example.authservice.usermanagement.services;

import com.example.authservice.dto.UserSyncDTO;
import com.example.authservice.exceptions.ConflictException;
import com.example.authservice.messaging.RabbitMQProducer;
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
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

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
        logger.info("User criado pelo mapper: {}", user);
        logger.info("PhoneNumber do User após mapeamento: {}", user.getPhoneNumber());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addAuthority(new Role("READER"));


        User savedUser = userRepo.save(user);

        logger.info("User salvo no banco de dados: {}", savedUser);
        logger.info("PhoneNumber salvo no banco: {}", savedUser.getPhoneNumber());

        syncUserWithOtherInstance(savedUser);

        return savedUser;
    }

    private void syncUserWithOtherInstance(User user) {
        logger.info("Sincronizando utilizador da instância {} para outra instância", instanceId);
        logger.info("Sincronizando utilizador: username={}, phoneNumber={}", user.getUsername(), user.getPhoneNumber());


        Set<String> authoritiesAsString = user.getAuthorities().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());

        UserSyncDTO userSyncDTO = new UserSyncDTO(
                user.getUsername(),
                user.getFullName(),
                user.getPassword(),
                user.isEnabled(),
                authoritiesAsString,
                instanceId,
                user.getPhoneNumber()
        );


        logger.info("UserSyncDTO criado para sincronização: {}", userSyncDTO);
        logger.info("PhoneNumber no UserSyncDTO: {}", userSyncDTO.getPhoneNumber());
        rabbitMQProducer.sendMessage("user.sync.create", userSyncDTO);
    }

    public String getOtherInstanceUrl() {
        if ("auth1".equals(instanceId)) {
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
        Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            return create(request);
        }

        User user = optionalUser.get();
        user.setFullName(request.getFullName());
        user.setAuthorities(request.getAuthorities().stream().map(Role::new).collect(Collectors.toSet()));

        return userRepo.save(user);
    }

    @Transactional
    public User delete(final Long id) {
        final User user = userRepo.getById(id);

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
