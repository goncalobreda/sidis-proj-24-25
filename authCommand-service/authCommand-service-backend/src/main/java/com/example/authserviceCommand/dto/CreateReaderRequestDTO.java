package com.example.authserviceCommand.dto;

import com.example.authserviceCommand.usermanagement.services.CreateUserRequest;
import com.example.authserviceCommand.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Data
@Schema(description = "DTO para registrar um novo leitor")
public class CreateReaderRequestDTO {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Email
    @NotBlank
    @Schema(description = "Email do leitor", example = "testuser@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Nome completo do leitor", example = "João Silva")
    private String fullName;

    @NotBlank
    @Schema(description = "Senha do leitor", example = "password123")
    private String password;

    @NotBlank
    @Schema(description = "Confirmação da senha", example = "password123")
    private String rePassword;

    @Schema(description = "Data de nascimento do leitor", example = "1990-01-01")
    private String birthdate;

    @Schema(description = "Interesses do leitor")
    private Set<String> interests;

    @NotBlank
    @Schema(description = "Número de telefone do leitor", example = "912345678")
    private String phoneNumber;

    @Schema(description = "Consentimento de GDPR", example = "true")
    private boolean GDPR;

    public CreateUserRequest toCreateUserRequest() {
        if (this.email == null || this.email.isBlank()) {
            throw new ValidationException("O campo 'email' é obrigatório e não pode estar vazio.");
        }

        logger.info("Convertendo CreateReaderRequestDTO para CreateUserRequest com username: {}", this.email);

        return new CreateUserRequest(
                this.email, // email como username
                this.fullName,
                this.password,
                this.phoneNumber
        );
    }



}
