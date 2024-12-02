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

    public void syncUserWithOtherInstance(User user) {
        if (instanceId.equals(user.getUsername())) {
            logger.info("Ignorando sincronização para a própria instância: {}", instanceId);
            return;
        }

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
                user.getPhoneNumber(),
                generateMessageId() // Novo campo messageId
        );

        rabbitMQProducer.sendMessage("user.sync.create", userSyncDTO);

        logger.info("Mensagem de sincronização enviada para o RabbitMQ: {}", userSyncDTO);
    }

    private String generateMessageId() {
        return java.util.UUID.randomUUID().toString();
    }






    @Transactional
    public User update(final Long id, final EditUserRequest request) {
        final User user = userRepo.getById(id);
        userEditMapper.update(request, user);

        User updatedUser = userRepo.save(user);

        logger.info("Utilizador atualizado: {}", updatedUser);

        // Sincronizar após a atualização
        syncUserWithOtherInstance(updatedUser);

        return updatedUser;
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

        logger.info("Iniciando upsert para utilizador: {}", request.getUsername());

        User upsertedUser = userRepo.save(user);

        // Sincronizar após o upsert
        syncUserWithOtherInstance(upsertedUser);

        logger.info("Upsert concluído para utilizador: {}", request.getUsername());

        return upsertedUser;
    }

    @Transactional
    public User delete(final Long id) {
        final User user = userRepo.getById(id);
        user.setEnabled(false);

        User disabledUser = userRepo.save(user);

        logger.info("Utilizador desativado: {}", disabledUser);

        // Sincronizar após desativação
        syncUserWithOtherInstance(disabledUser);

        return disabledUser;
    }
}
