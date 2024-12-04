package com.example.lendingserviceCommand.service;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CreateLendingRequest {

    @NotNull
    private Long bookID;

    @NotNull
    private String readerID;
}
