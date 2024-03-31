package com.pishgaman.phonebook.specifications;


import com.pishgaman.phonebook.searchforms.UserSearch;
import com.pishgaman.phonebook.security.user.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {

    public static Specification<User> getSpecification(UserSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (search.getFirstname() != null) {
                predicate = cb.and(predicate, cb.like(root.get("firstname"), "%" + search.getFirstname() + "%"));
            }
            if (search.getLastname() != null) {
                predicate = cb.and(predicate, cb.like(root.get("lastname"), "%" + search.getLastname() + "%"));
            }
            if (search.getEmail() != null) {
                predicate = cb.and(predicate, cb.like(root.get("email"), "%" + search.getEmail() + "%"));
            }
            return predicate;
        };
    }
}

