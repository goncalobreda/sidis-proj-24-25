package com.example.authserviceCommand.bootstrap;

import com.example.authserviceCommand.usermanagement.model.Role;
import com.example.authserviceCommand.usermanagement.model.User;
import com.example.authserviceCommand.dto.UserSyncDTO;
import com.example.authserviceCommand.messaging.RabbitMQProducer;
import com.example.authserviceCommand.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
    private final RabbitMQProducer rabbitMQProducer;

    @Value("${instance.id}")
    private String instanceId;

    @Override
    public void run(final String... args) {
        System.out.println("Bootstrap running for instance: " + instanceId);

        createAndSyncUser("librarian1@mail.com", "pass1");
        createAndSyncUser("librarian2@mail.com", "pass2");

        if ("command.auth1".equals(instanceId)) {
            System.out.println("Instância 'command.auth1'. Enviando sinal de bootstrap para RabbitMQ...");
            sendBootstrapSignal();
        } else {
            System.out.println("Instância '" + instanceId + "'. Realizando apenas bootstrap local.");
            performLocalBootstrap();
        }
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
                    savedUser.getPhoneNumber(),
                    generateMessageId() // Novo campo messageId
            );

            if ("command.auth1".equals(instanceId)) {
                rabbitMQProducer.sendMessage("user.sync.create", userSyncDTO);
                System.out.println("Utilizador sincronizado via RabbitMQ: " + savedUser.getUsername());
            } else {
                System.out.println("Utilizador criado apenas localmente: " + savedUser.getUsername());
            }
        }
    }

    private String generateMessageId() {
        return java.util.UUID.randomUUID().toString();
    }


    private void sendBootstrapSignal() {
        List<UserSyncDTO> bootstrapUsers = userRepo.findAll().stream()
                .peek(user -> Hibernate.initialize(user.getAuthorities())) // Inicializa as autoridades
                .map(user -> new UserSyncDTO(
                        user.getUsername(),
                        user.getFullName(),
                        user.getPassword(),
                        user.isEnabled(),
                        user.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toSet()),
                        instanceId,
                        user.getPhoneNumber(),
                        generateMessageId() // Adicionado messageId
                ))
                .collect(Collectors.toList());

        rabbitMQProducer.sendBootstrapMessage(bootstrapUsers);
        System.out.println("Sinal de bootstrap enviado com lista de utilizadores.");
    }


    private void performLocalBootstrap() {
        userRepo.findAll().forEach(user -> System.out.println("Utilizador carregado localmente: " + user.getUsername()));
        System.out.println("Bootstrap local concluído para instância: " + instanceId);
    }
}
