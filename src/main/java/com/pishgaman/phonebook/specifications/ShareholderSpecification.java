package com.pishgaman.phonebook.specifications;
import com.pishgaman.phonebook.entities.Shareholder;
import com.pishgaman.phonebook.searchforms.ShareholderSearchForm;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ShareholderSpecification {

    public static Specification<Shareholder> bySearchForm(ShareholderSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchForm.getCompanyId() != null){
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), searchForm.getCompanyId()));
            }

            if (searchForm.getPersonName() != null) {
                Predicate personNamePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(root.get("person").get("firstName"), "%" + searchForm.getPersonName() + "%"),
                        criteriaBuilder.like(root.get("person").get("lastName"), "%" + searchForm.getPersonName() + "%")
                );
                predicates.add(personNamePredicate);
            }

            if (searchForm.getCompanyName() != null) {
                Predicate companyNamePredicate = criteriaBuilder.like(root.get("company").get("name"), "%" + searchForm.getCompanyName() + "%");
                predicates.add(companyNamePredicate);
            }

            if (searchForm.getShareType() != null) {
                Predicate shareTypePredicate = criteriaBuilder.equal(root.get("shareType"), Shareholder.ShareType.valueOf(searchForm.getShareType().toUpperCase()));
                predicates.add(shareTypePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

