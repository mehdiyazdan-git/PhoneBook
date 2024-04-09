package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import com.pishgaman.phonebook.searchforms.InsuranceSlipSearchForm;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InsuranceSlipSpecification {

    public static Specification<InsuranceSlip> bySearchForm(InsuranceSlipSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchForm.getCompanyId() != null){
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

            if (searchForm.getSlipNumber() != null) {
                Predicate slipNumberPredicate = criteriaBuilder.like(root.get("slipNumber"), "%" + searchForm.getSlipNumber() + "%");
                predicates.add(slipNumberPredicate);
            }

            if (searchForm.getType() != null) {
                Predicate typePredicate = criteriaBuilder.equal(root.get("type"), InsuranceSlip.SlipType.valueOf(searchForm.getType().name().toUpperCase()));
                predicates.add(typePredicate);
            }

            if (searchForm.getAmount() != null) {
                Predicate amountPredicate = criteriaBuilder.equal(root.get("amount"), searchForm.getAmount());
                predicates.add(amountPredicate);
            }


            if (searchForm.getStartDate() != null && !searchForm.getStartDate().isEmpty()) {
                String trimmed = searchForm.getStartDate().trim();
                String[] dateParts = trimmed.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                predicates.add(criteriaBuilder.equal(root.get("startDate"), date));
            }

            if (searchForm.getEndDate() != null && !searchForm.getEndDate().isEmpty()) {
                String trimmed = searchForm.getEndDate().trim();
                String[] dateParts = trimmed.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                predicates.add(criteriaBuilder.equal(root.get("endDate"), date));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
