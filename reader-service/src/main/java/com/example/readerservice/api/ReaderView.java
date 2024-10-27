package com.example.readerservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "A Reader")
public class ReaderView {

    @Schema(description = "The name of the reader")
    private String fullName;

    @Schema(description = "The readerID of the reader")
    private String readerID;

    @Schema(description = "The email of the reader")
    private String email;

    @Schema(description = "The birthdate of the reader")
    private String birthdate;

    @Schema(description = "The phone number of the reader")
    private String phoneNumber;

    @Schema(description = "The interests of the reader")
    private Set<String> interests;

    @Schema(description = "The image URL of the reader")
    private String imageUrl;
}
