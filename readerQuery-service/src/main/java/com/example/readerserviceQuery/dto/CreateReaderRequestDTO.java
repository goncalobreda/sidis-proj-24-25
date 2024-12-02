package com.example.readerserviceQuery.dto;

import com.example.readerserviceQuery.service.CreateReaderRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "DTO para registrar um novo leitor")
public class CreateReaderRequestDTO {

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

    public CreateReaderRequest toCreateReaderRequest() {
        return new CreateReaderRequest(
                this.email,
                this.fullName,
                this.password,
                this.rePassword,
                this.birthdate,
                this.interests,
                this.phoneNumber,
                this.GDPR
        );
    }

}
