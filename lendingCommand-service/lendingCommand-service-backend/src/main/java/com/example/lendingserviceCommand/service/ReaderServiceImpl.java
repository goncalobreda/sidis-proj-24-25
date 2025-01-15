package com.example.lendingserviceCommand.service;

import com.example.lendingserviceCommand.repositories.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;

    @Override
    public boolean existsByReaderId(String readerId) {
        return readerRepository.existsByReaderId(readerId);
    }
}
