package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LetterSpecification {
    public static Specification<Letter> getSpecification(LetterSearch search) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (search.getId() != null) {
                predicate = cb.and(predicate, cb.like(root.get("Id"), "%" + search.getId() + "%"));
            }
            if (search.getCreationDate() != null && !search.getCreationDate().isEmpty()) {
                LocalDate birthDate = LocalDate.parse(search.getCreationDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicate = cb.and(predicate, cb.equal(root.get("creationDate"), birthDate));
            }
            if (search.getContent() != null && !search.getContent().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("content"), "%" + search.getContent() + "%"));
            }
            if (search.getLetterNumber() != null && !search.getLetterNumber().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("letterNumber"), "%" + search.getLetterNumber() + "%"));
            }
            if (search.getCustomerName() != null && !search.getCustomerName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("recipientName"), "%" + search.getCustomerName() + "%"));
            }
            if (search.getCompanyName() != null && !search.getCompanyName().isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("senderName"), "%" + search.getCompanyName() + "%"));
            }

            return predicate;
        };
    }
}
