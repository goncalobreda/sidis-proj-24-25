package com.example.lendingserviceCommand.service;

import com.example.lendingserviceCommand.dto.CreateLendingDTO;
import com.example.lendingserviceCommand.model.Lending;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LendingService {

    Iterable<Lending> findAll();
    Optional<Lending> findById(int id1, int id2);
    //String getOtherInstanceUrl();

    Optional<String> findReaderByLendingID(String lendingID);  // Retorna apenas o ID do Reader
    Optional<Lending> getLastId();
    List<Lending> getOverdueLendingsSortedByTardiness();
    Lending create(CreateLendingDTO dto);


    Lending partialUpdate(int id1, int id2, EditLendingRequest resource, long desiredVersion);

    int calculateFine(String lendingID);


    double getAverageLendingDuration();





    Map<String, Double> getAverageLendingsPerGenre(int month, int year);



}
