package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.LetterType;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LetterSearchDao {
    private final EntityManager entityManager;

    public Page<LetterDetailsDto> findAllBySimpleQuery(
            LetterSearch search,
            Integer page,
            Integer size,
            String sortBy,
            String order
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LetterDetailsDto> criteriaQuery = criteriaBuilder.createQuery(LetterDetailsDto.class);
        Root<Letter> root = criteriaQuery.from(Letter.class);

        List<Predicate> predicates = preparePredicates(criteriaBuilder, root, search);

        Path<Object> querySortBy = getSortPath(root, sortBy);
        Order queryOrder = getOrder(criteriaBuilder, querySortBy, order);

        criteriaQuery.orderBy(queryOrder);
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        criteriaQuery.select(criteriaBuilder.construct(
                LetterDetailsDto.class,
                root.get("id"),
                root.get("content"),
                root.get("creationDate"),
                root.get("letterNumber"),
                root.join("customer", JoinType.LEFT).get("id"),
                root.join("customer", JoinType.LEFT).get("name"),
                root.join("company", JoinType.LEFT).get("id"),
                root.join("company", JoinType.LEFT).get("companyName"),
                root.join("year", JoinType.LEFT).get("id")
        ));

        TypedQuery<LetterDetailsDto> query = entityManager
                .createQuery(criteriaQuery)
                .setFirstResult(page * size)
                .setMaxResults(size);
        List<LetterDetailsDto> resultList = query.getResultList();

        int totalItems = totalItemsCount(search);

        return new PageImpl<>(resultList, PageRequest.of(page, size), totalItems);
    }

    // Helper method to get Path for sorting
    private Path<Object> getSortPath(Root<Letter> root, String sortBy) {
        if ("customerName".equals(sortBy)) {
            return root.join("customer", JoinType.LEFT).get("name");
        } else {
            return root.get(sortBy);
        }
    }

    // Helper method to get Order object
    private Order getOrder(CriteriaBuilder cb, Path<Object> sortByQuery, String order) {
        if (order != null && order.equalsIgnoreCase("desc")) {
            return cb.desc(sortByQuery);
        } else {
            return cb.asc(sortByQuery);
        }
    }

    private List<Predicate> preparePredicates(CriteriaBuilder criteriaBuilder, Root<Letter> root, LetterSearch search) {
        List<Predicate> predicates = new ArrayList<>();

        if (search.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"),search.getId()));
        }
        if (search.getYearId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("year").get("id"), search.getYearId()));
        }
        if (search.getCreationDate() != null && !search.getCreationDate().isEmpty()) {
            LocalDate birthDate = LocalDate.parse(search.getCreationDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            predicates.add(criteriaBuilder.equal(root.get("creationDate"), birthDate));
        }
        if (search.getContent() != null) {
            predicates.add(criteriaBuilder.like(root.get("content"), "%" + search.getContent() + "%"));
        }
        if (search.getLetterNumber() != null) {
            predicates.add(criteriaBuilder.like(root.get("letterNumber"), "%" + search.getLetterNumber() + "%"));
        }
        if (search.getCompanyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("company").get("id"), search.getCompanyId()));
        }
        if (search.getLetterType() != null) {
            Join<Letter, LetterType> letterTypeJoin = root.join("letterType");
            predicates.add(criteriaBuilder.equal(letterTypeJoin.get("type"), search.getLetterType()));
        }

        if (search.getCustomerName() != null) {
            predicates.add(criteriaBuilder
                    .like(root.join("customer")
                            .get("name"), "%" + search.getCustomerName() + "%"));
        }
        return predicates;
    }
    private int totalItemsCount(LetterSearch search) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Letter> countRoot = countQuery.from(Letter.class);

        List<Predicate> countPredicates = preparePredicates(criteriaBuilder, countRoot, search);

        countQuery.select(criteriaBuilder.count(countRoot))
                .where(criteriaBuilder.and(countPredicates.toArray(new Predicate[0])));

        return Math.toIntExact(entityManager.createQuery(countQuery).getSingleResult());
    }
}
