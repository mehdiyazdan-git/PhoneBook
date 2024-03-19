package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Category;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification implements Specification<Category> {

    private final String fieldName;
    private final Object value;

    public CategorySpecification(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }


    public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
            // Assuming that we are selecting a DTO and not the Category entity itself.
            // This join is necessary for selecting the DTO in the constructor expression.
            root.join("products", JoinType.LEFT);
        }

        if ("name".equals(fieldName)) {
            return criteriaBuilder.like(root.get("name"), "%" + value.toString() + "%");
        } else if ("id".equals(fieldName)) {
            return criteriaBuilder.equal(root.get("id"), value);
        }

        // You can add more conditions here based on your requirements.

        return null; // if none of the above, return null or a default Predicate
    }
}
