package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Product;
import com.pishgaman.phonebook.searchforms.ProductSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> getSpecification(ProductSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (search.getName() != null) {
                predicate = cb.and(predicate, cb.like(root.get("name"),  search.getName() + "%"));
            }
            if (search.getDescription() != null) {
                predicate = cb.and(predicate, cb.like(root.get("description"), "%" + search.getDescription() + "%"));
            }
            if (search.getPrice() != null && search.getPriceComparison() != null) {
                predicate = switch (search.getPriceComparison()) {
                    case "gt" -> cb.and(predicate, cb.greaterThan(root.get("price"), search.getPrice()));
                    case "lt" -> cb.and(predicate, cb.lessThan(root.get("price"), search.getPrice()));
                    case "ge" -> cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), search.getPrice()));
                    case "le" -> cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), search.getPrice()));
                    default ->
                        // Handle invalid comparison type or fallback to exact match
                            cb.and(predicate, cb.equal(root.get("price"), search.getPrice()));
                };
            }
            return predicate;
        };
    }
}
