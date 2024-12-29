package com.example.acquisitionserviceQuery.repositories;

import com.example.acquisitionserviceQuery.model.Acquisition;
import com.example.acquisitionserviceQuery.model.AcquisitionStatus;

import java.util.List;
import java.util.Optional;

public interface AcquisitionRepository {

    List<Acquisition> findAll();

    List<Acquisition> findByStatus(AcquisitionStatus status);

    Optional<Acquisition> findTopByOrderByAcquisitionIDDesc();

    boolean existsByIsbn(String isbn);

    <S extends Acquisition> S save(S entity);

    Optional<Acquisition> findById(String id);

    Optional<Acquisition> findByIsbn(String isbn);
}
