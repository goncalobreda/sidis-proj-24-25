package com.example.readerserviceCommand.infrastructure.repositories.impl;


import com.example.readerserviceCommand.model.Reader;
import com.example.readerserviceCommand.service.Page;
import com.example.readerserviceCommand.service.SearchReadersQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
class ReaderRepoCustomImpl implements ReaderRepoCustom {
    private final EntityManager em;

    @Override
    public List<Reader> searchReaders(final Page page, final SearchReadersQuery query) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Reader> cq = cb.createQuery(Reader.class);
        final Root<Reader> root = cq.from(Reader.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(query.getName())) {
            where.add(cb.like(cb.lower(root.get("name")), "%" + query.getName().toLowerCase() + "%"));
        }
        if (StringUtils.hasText(query.getEmail())) {
            where.add(cb.equal(root.get("email"), query.getEmail()));
        }
        if (StringUtils.hasText(query.getPhoneNumber())) {
            where.add(cb.like(root.get("phoneNumber"), query.getPhoneNumber()));
        }

        if (!where.isEmpty()) {	// search using OR
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.desc(root.get("createdAt")));

        final TypedQuery<Reader> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}
