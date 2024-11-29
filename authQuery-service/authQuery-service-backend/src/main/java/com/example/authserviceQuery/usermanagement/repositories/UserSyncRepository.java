package com.example.authserviceQuery.usermanagement.repositories;

import com.example.authserviceQuery.usermanagement.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSyncRepository extends CrudRepository<User, Long> {
    // Apenas para sincronização no Query Service
}
