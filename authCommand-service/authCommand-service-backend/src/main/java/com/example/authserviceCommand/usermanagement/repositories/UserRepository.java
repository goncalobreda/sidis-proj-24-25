package com.example.authserviceCommand.usermanagement.repositories;

import com.example.authserviceCommand.exceptions.NotFoundException;
import com.example.authserviceCommand.usermanagement.model.User;
import com.example.authserviceCommand.usermanagement.services.Page;
import com.example.authserviceCommand.usermanagement.services.SearchUsersQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {

    <S extends User> List<S> saveAll(Iterable<S> entities);

    <S extends User> S save(S entity);

    Optional<User> findById(Long objectId);

    default User getById(final Long id) {
        final Optional<User> maybeUser = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeUser.filter(User::isEnabled).orElseThrow(() -> new NotFoundException(User.class, id));
    }

    Optional<User> findByUsername(String username);

}
