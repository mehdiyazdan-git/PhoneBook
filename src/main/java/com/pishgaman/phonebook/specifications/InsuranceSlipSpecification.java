package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import com.pishgaman.phonebook.searchforms.InsuranceSlipSearchForm;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InsuranceSlipSpecification {

    public static Specification<InsuranceSlip> bySearchForm(InsuranceSlipSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

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

            if (searchForm.getStartDate() != null) {
                Predicate startDatePredicate = criteriaBuilder.equal(root.get("startDate"), searchForm.getStartDate());
                predicates.add(startDatePredicate);
            }

            if (searchForm.getEndDate() != null) {
                Predicate endDatePredicate = criteriaBuilder.equal(root.get("endDate"), searchForm.getEndDate());
                predicates.add(endDatePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
