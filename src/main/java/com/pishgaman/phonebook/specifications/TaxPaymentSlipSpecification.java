package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import com.pishgaman.phonebook.searchforms.TaxPaymentSlipSearchForm;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaxPaymentSlipSpecification {

    public static Specification<TaxPaymentSlip> bySearchForm(TaxPaymentSlipSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchForm.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), searchForm.getCompanyId()));
            }


            if (searchForm.getCompanyName() != null) {
                Predicate companyNamePredicate = criteriaBuilder.like(root.get("company").get("name"), "%" + searchForm.getCompanyName() + "%");
                predicates.add(companyNamePredicate);
            }

            if (searchForm.getType() != null) {
                Predicate slipTypePredicate = criteriaBuilder.equal(root.get("slipType"), TaxPaymentSlip.TaxPaymentSlipType.valueOf(searchForm.getType().name().toUpperCase()));
                predicates.add(slipTypePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

