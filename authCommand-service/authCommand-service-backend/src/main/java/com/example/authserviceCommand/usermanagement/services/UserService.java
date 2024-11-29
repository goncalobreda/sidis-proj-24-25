package com.example.authserviceCommand.usermanagement.services;

import com.example.authserviceCommand.dto.UserSyncDTO;
import com.example.authserviceCommand.exceptions.ConflictException;
import com.example.authserviceCommand.messaging.RabbitMQProducer;
import com.example.authserviceCommand.usermanagement.model.Role;
import com.example.authserviceCommand.usermanagement.model.User;
import com.example.authserviceCommand.usermanagement.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;
    private final EditUserMapper userEditMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}") // ID único para identificar a instância (auth1 ou auth2)
    private String instanceId;

    @Transactional
    public User create(final CreateUserRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        final User user = userEditMapper.create(request);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.addAuthority(new Role("READER"));

        User savedUser = userRepo.save(user);

        logger.info("User salvo no banco de dados: {}", savedUser);

        syncUserWithOtherInstance(savedUser);

        return savedUser;
    }

    private void syncUserWithOtherInstance(User user) {
        logger.info("Sincronizando utilizador da instância {} para outra instância", instanceId);

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

        rabbitMQProducer.sendMessage("user.sync.create", userSyncDTO);
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
}
