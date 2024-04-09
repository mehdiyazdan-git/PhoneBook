package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import com.pishgaman.phonebook.searchforms.TaxPaymentSlipSearchForm;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaxPaymentSlipSpecification {

    public static Specification<TaxPaymentSlip> bySearchForm(TaxPaymentSlipSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchForm.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), searchForm.getCompanyId()));
            }
            if (searchForm.getIssueDate() != null && !searchForm.getIssueDate().isEmpty()) {
                String trimmed = searchForm.getIssueDate().trim();
                String[] dateParts = trimmed.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                predicates.add(criteriaBuilder.equal(root.get("issueDate"), date));
            }


            if (searchForm.getCompanyName() != null) {
                Predicate companyNamePredicate = criteriaBuilder.like(root.get("company").get("name"), "%" + searchForm.getCompanyName() + "%");
                predicates.add(companyNamePredicate);
            }

            if (searchForm.getType() != null) {
                Predicate slipTypePredicate = criteriaBuilder.equal(root.get("type"), TaxPaymentSlip.TaxPaymentSlipType.valueOf(searchForm.getType().name().toUpperCase()));
                predicates.add(slipTypePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

