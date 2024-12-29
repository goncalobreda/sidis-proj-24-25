package com.example.acquisitionserviceCommand.repositories;

import com.example.acquisitionserviceCommand.model.Acquisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcquisitionRepository extends JpaRepository<Acquisition, String> {

    Optional<Acquisition> findTopByOrderByAcquisitionIDDesc();

    boolean existsByIsbn(String isbn);

    boolean existsByTitle(String title);

    Optional<Acquisition> findByAcquisitionID(String acquisitionID);

    Optional<Acquisition> findByIsbn(String isbn);

}
