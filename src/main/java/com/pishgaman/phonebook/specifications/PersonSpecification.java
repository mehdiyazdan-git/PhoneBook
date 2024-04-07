package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.searchforms.PersonSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonSpecification {

    public static Specification<Person> getSpecification(PersonSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (search.getFirstName() != null && !search.getFirstName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("firstName"), "%" + search.getFirstName() + "%"));
            }
            if (search.getLastName() != null && !search.getLastName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("lastName"), "%" + search.getLastName() + "%"));
            }
            if (search.getFatherName() != null && !search.getFatherName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("fatherName"), "%" + search.getFatherName() + "%"));
            }
            if (search.getNationalId() != null && !search.getNationalId().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("nationalId"), "%" + search.getNationalId() + "%"));
            }
            if (search.getBirthDate() != null && !search.getBirthDate().isEmpty()) {
                LocalDate birthDate = LocalDate.parse(search.getBirthDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicate = cb.and(predicate, cb.equal(root.get("birthDate"), birthDate));
            }
            if (search.getRegistrationNumber() != null && !search.getRegistrationNumber().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("registrationNumber"), "%" + search.getRegistrationNumber() + "%"));
            }
            if (search.getPostalCode() != null && !search.getPostalCode().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("postalCode"), "%" + search.getPostalCode() + "%"));
            }
            if (search.getAddress() != null && !search.getAddress().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("address"), "%" + search.getAddress() + "%"));
            }

            return predicate;
        };
    }
    public static Specification<Person> getSelectSpecification(String searchParam){

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (searchParam !=null && !searchParam.isEmpty()) {
                predicate =(cb.like(root.get("name"), "%" + searchParam.trim() + "%"));
            }
            return predicate;
        };

    }
}
