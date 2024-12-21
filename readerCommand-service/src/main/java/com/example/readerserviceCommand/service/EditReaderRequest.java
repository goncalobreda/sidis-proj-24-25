package com.example.readerserviceCommand.service;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EditReaderRequest {

    @Pattern(regexp = "[1-9][0-9]{8}")
    private String phoneNumber;


    @JsonCreator
    public EditReaderRequest(@JsonProperty("phoneNumber") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "EditReaderRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
