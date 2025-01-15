package com.example.lendingserviceCommand.service;

public interface ReaderService {
    /**
     * Verifica se um Reader com o dado readerId (ex: "2024/10")
     * existe localmente na base do LendingCommand.
     */
    boolean existsByReaderId(String readerId);
}
