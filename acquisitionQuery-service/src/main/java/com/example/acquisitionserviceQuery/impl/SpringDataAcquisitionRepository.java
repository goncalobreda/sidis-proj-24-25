package com.example.acquisitionserviceQuery.impl;

import com.example.acquisitionserviceQuery.model.Acquisition;
import com.example.acquisitionserviceQuery.model.AcquisitionStatus;
import com.example.acquisitionserviceQuery.repositories.AcquisitionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataAcquisitionRepository extends AcquisitionRepository, CrudRepository<Acquisition, String> {

    @Override
    @Query("SELECT a FROM Acquisition a")
    List<Acquisition> findAll();

    @Override
    @Query("SELECT a FROM Acquisition a WHERE a.status = :status")
    List<Acquisition> findByStatus(@Param("status") AcquisitionStatus status);

    @Override
    @Query("SELECT a FROM Acquisition a ORDER BY a.acquisitionID DESC LIMIT 1")
    Optional<Acquisition> findTopByOrderByAcquisitionIDDesc();

    @Override
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Acquisition a WHERE a.isbn = :isbn")
    boolean existsByIsbn(@Param("isbn") String isbn);
}
