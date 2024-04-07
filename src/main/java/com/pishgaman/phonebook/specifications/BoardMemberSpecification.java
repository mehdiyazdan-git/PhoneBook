package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.BoardMember;
import com.pishgaman.phonebook.searchforms.BoardMemberSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BoardMemberSpecification {

    public static Specification<BoardMember> getSpecification(BoardMemberSearch search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search.getId() != null) {
                predicates.add(cb.equal(root.get("id"), search.getId()));
            }
            if (search.getCompanyId() != null){
                predicates.add(cb.equal(root.get("company").get("id"), search.getCompanyId()));
            }

            if (search.getFullName() != null && !search.getFullName().isEmpty()) {
                String fullName = search.getFullName().toLowerCase();
                Predicate firstNamePredicate = cb.like(cb.lower(root.get("person").get("firstName")), "%" + fullName + "%");
                Predicate lastNamePredicate = cb.like(cb.lower(root.get("person").get("lastName")), "%" + fullName + "%");
                Predicate fullNamePredicate = cb.or(firstNamePredicate, lastNamePredicate);
                predicates.add(fullNamePredicate);
            }

            if (search.getPersonId() != null) {
                predicates.add(cb.equal(root.get("person").get("id"), search.getPersonId()));
            }

            if (search.getPersonFirstName() != null && !search.getPersonFirstName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("person").get("firstName")), "%" + search.getPersonFirstName().toLowerCase() + "%"));
            }

            if (search.getPersonLastName() != null && !search.getPersonLastName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("person").get("lastName")), "%" + search.getPersonLastName().toLowerCase() + "%"));
            }

            if (search.getCompanyCompanyName() != null && !search.getCompanyCompanyName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("company").get("companyName")), "%" + search.getCompanyCompanyName().toLowerCase() + "%"));
            }

            if (search.getPositionName() != null && !search.getPositionName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("position").get("name")), "%" + search.getPositionName().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
