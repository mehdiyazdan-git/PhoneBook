package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.searchforms.PositionSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PositionSpecification {
    public static Specification<Position> getSpecification(PositionSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (search.getName() != null) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + search.getName() + "%"));
            }
            return predicate;
        };
    }
    public static Specification<Position> getSelectSpecification(String searchParam){

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (searchParam !=null && !searchParam.isEmpty()) {
                predicate =(cb.like(root.get("name"), "%" + searchParam.trim() + "%"));
            }
            return predicate;
        };

    }
}
