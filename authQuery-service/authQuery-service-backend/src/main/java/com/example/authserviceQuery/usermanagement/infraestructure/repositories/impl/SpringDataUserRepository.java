package com.example.authserviceQuery.usermanagement.infraestructure.repositories.impl;

import com.example.authserviceQuery.exceptions.NotFoundException;
import com.example.authserviceQuery.usermanagement.model.User;
import com.example.authserviceQuery.usermanagement.repositories.UserRepository;
import com.example.authserviceQuery.usermanagement.services.Page;
import com.example.authserviceQuery.usermanagement.services.SearchUsersQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@CacheConfig(cacheNames = "users")
public interface SpringDataUserRepository extends UserRepository, UserRepoCustom, CrudRepository<User, Long> {

    @Override
    @Cacheable
    Optional<User> findById(Long objectId);

    @Override
    @Cacheable
    default User getById(final Long id) {
        final Optional<User> maybeUser = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeUser.filter(User::isEnabled).orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Override
    @Cacheable
    Optional<User> findByUsername(String username);
}


interface UserRepoCustom {

    List<User> searchUsers(Page page, SearchUsersQuery query);
}


@RequiredArgsConstructor
@Repository
class UserRepoCustomImpl implements UserRepoCustom {

    private final EntityManager em;

    @Override
    public List<User> searchUsers(final Page page, final SearchUsersQuery query) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> root = cq.from(User.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(query.getUsername())) {
            where.add(cb.equal(root.get("username"), query.getUsername()));
        }
        if (StringUtils.hasText(query.getFullName())) {
            where.add(cb.like(root.get("fullName"), "%" + query.getFullName() + "%"));
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.desc(root.get("createdAt")));

        final TypedQuery<User> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}
