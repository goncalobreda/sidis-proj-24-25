package com.example.readerserviceCommand.service;

import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.model.ReaderCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface ReaderService {

    // Métodos relacionados a comando (criação, atualização, sincronização)
    Reader create(CreateReaderRequest request);

    Reader partialUpdate(String readerID, EditReaderRequest request, long parseLong);

    Reader partialUpdateFromConsumer(String readerID, EditReaderRequest request, long parseLong);

    void syncReceivedReader(Reader reader);

    // Validação
    void validateBirthdate(String birthdate);
}
