package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.searchforms.YearSearch;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class YearSpecification {

    public static Specification<Year> getSpecification(YearSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (search != null && search.getName() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("name"), search.getName()));
            }
            return predicate;
        };
    }
}

