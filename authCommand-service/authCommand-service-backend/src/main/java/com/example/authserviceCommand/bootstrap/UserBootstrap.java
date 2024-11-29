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

    @Value("${instance.id}") // ID único da instância (command.auth1 ou command.auth2)
    private String instanceId;

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("Bootstrap running...");

        // Criação e sincronização dos utilizadores
        createAndSyncUser("librarian1@mail.com", "pass1");
        createAndSyncUser("librarian2@mail.com", "pass2");
        createAndSyncUser("librarian3@mail.com", "pass3");
        createAndSyncUser("librarian4@mail.com", "pass4");
        createAndSyncUser("librarian5@mail.com", "pass5");
        createAndSyncUser("librarian6@mail.com", "pass6");
        createAndSyncUser("librarian7@mail.com", "pass7");
        createAndSyncUser("librarian8@mail.com", "pass8");
        createAndSyncUser("librarian9@mail.com", "pass9");
        createAndSyncUser("librarian10@mail.com", "pass10");
    }

    private void createAndSyncUser(String email, String password) {
        // Verifica se o utilizador já existe
        if (userRepo.findByUsername(email).isEmpty()) {
            // Cria o utilizador
            final var user = new User(email, encoder.encode(password));
            user.addAuthority(new Role(Role.LIBRARIAN));

            // Salva no banco de dados do Command
            User savedUser = userRepo.save(user);

            // Converte o utilizador para UserSyncDTO
            UserSyncDTO userSyncDTO = new UserSyncDTO(
                    savedUser.getUsername(),
                    savedUser.getFullName(),
                    savedUser.getPassword(),
                    savedUser.isEnabled(),
                    savedUser.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toSet()),
                    instanceId, // Inclui o ID da instância de origem
                    savedUser.getPhoneNumber()
            );

            // Envia evento para o Query através do RabbitMQ com uma routing key dinâmica
            String routingKey = "user.sync." + instanceId;
            rabbitMQProducer.sendMessage(routingKey, userSyncDTO);

            System.out.println("Utilizador sincronizado: " + savedUser.getUsername());
        }
    }
}
