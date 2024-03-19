package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.searchforms.SenderSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SenderSpecification {
    public static Specification<Sender> getSpecification(SenderSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (search.getName() != null) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + search.getName() + "%"));
            }
            if (search.getAddress() != null) {
                predicate = cb.and(predicate, cb.like(root.get("address"), "%" + search.getAddress() + "%"));
            }
            if (search.getPhoneNumber() != null) {
                predicate = cb.and(predicate, cb.like(root.get("phoneNumber"), "%" + search.getPhoneNumber() + "%"));
            }
            return predicate;
        };
    }
}
