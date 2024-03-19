package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.searchforms.CustomerSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerSpecification {
    public static Specification<Customer> getSpecification(CustomerSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (search.getName() != null && !search.getName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + search.getName() + "%"));
            }
            if (search.getAddress() != null && !search.getAddress().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("address"), "%" + search.getAddress() + "%"));
            }
            if (search.getPhoneNumber() != null && !search.getPhoneNumber().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("phoneNumber"), "%" + search.getPhoneNumber() + "%"));
            }
            if (search.getNationalIdentity() != null && !search.getNationalIdentity().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nationalIdentity"), "%" + search.getNationalIdentity() + "%"));
            }
            if (search.getRegisterCode() != null && !search.getRegisterCode().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("registerCode"), "%" + search.getRegisterCode() + "%"));
            }
            if (search.getRegisterDate() != null && !search.getRegisterDate().isEmpty()) {
                LocalDate registerDate = LocalDate.parse(search.getRegisterDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicate = cb.and(predicate, cb.equal(root.get("registerDate"), registerDate));
            }
            return predicate;
        };
    }
    public static Specification<Customer> getSelectSpecification(String searchParam){
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (searchParam !=null && !searchParam.isEmpty()) {
                predicate =(cb.like(root.get("name"), "%" + searchParam.trim() + "%"));
            }
            return predicate;
        };

    }
}
