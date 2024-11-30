package com.example.authserviceCommand.bootstrap;

import com.example.authserviceCommand.usermanagement.model.Role;
import com.example.authserviceCommand.usermanagement.model.User;
import com.example.authserviceCommand.dto.UserSyncDTO;
import com.example.authserviceCommand.messaging.RabbitMQProducer;
import com.example.authserviceCommand.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    public void run(final String... args) throws Exception {
        System.out.println("Bootstrap running...");

        // Criação e sincronização dos utilizadores
        createAndSyncUser("librarian1@mail.com", "pass1");
        createAndSyncUser("librarian2@mail.com", "pass2");

        // Após sincronizar os utilizadores, enviar uma mensagem de bootstrap para AuthQuery
        sendBootstrapSignal();
    }

    private void createAndSyncUser(String email, String password) {
        if (userRepo.findByUsername(email).isEmpty()) {
            final var user = new User(email, encoder.encode(password));
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

            String routingKey = "user.sync." + instanceId;
            rabbitMQProducer.sendMessage(routingKey, userSyncDTO);
            System.out.println("Utilizador sincronizado: " + savedUser.getUsername());
        }
    }

    private void sendBootstrapSignal() {
        String bootstrapMessage = "Bootstrap completo pela instância " + instanceId;
        rabbitMQProducer.sendBootstrapMessage(bootstrapMessage);
        System.out.println("Sinal de bootstrap enviado.");
    }
}
