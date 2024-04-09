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
                Predicate companyIdPredicate = criteriaBuilder.equal(root.get("company").get("id"), searchForm.getCompanyId());
                predicates.add(companyIdPredicate);
            }
            if (searchForm.getPersonFirstName() != null && !searchForm.getPersonFirstName().isEmpty()) {
                Predicate firstNamePredicate = criteriaBuilder.like(root.get("person").get("firstName"),"%" + searchForm.getPersonFirstName() + "%");
                predicates.add(firstNamePredicate);
            }
            if (searchForm.getPersonLastName() != null && !searchForm.getPersonLastName().isEmpty()) {
                Predicate lastNamePredicate = criteriaBuilder.like(root.get("person").get("lastName"), "%" + searchForm.getPersonLastName() + "%");
                predicates.add(lastNamePredicate);
            }
            if (searchForm.getCompanyName() != null) {
                Predicate companyNamePredicate = criteriaBuilder.like(root.get("company").get("companyName"), "%" + searchForm.getCompanyName() + "%");
                predicates.add(companyNamePredicate);
            }
            if (searchForm.getShareType() != null && !searchForm.getShareType().name().isEmpty()) {
                Predicate shareTypePredicate = criteriaBuilder.equal(root.get("shareType"), Shareholder.ShareType.valueOf(searchForm.getShareType().name().toUpperCase()));
                predicates.add(shareTypePredicate);
            }
            if (searchForm.getPercentageOwnership() != null){
                Predicate percentageOwnershipPredicate = criteriaBuilder.equal(root.get("percentageOwnership"), searchForm.getPercentageOwnership());
                predicates.add(percentageOwnershipPredicate);
            }
            if (searchForm.getSharePrice() != null){
                Predicate sharePricePredicate = criteriaBuilder.equal(root.get("sharePrice"), searchForm.getSharePrice());
                predicates.add(sharePricePredicate);
            }
            if (searchForm.getNumberOfShares() != null){
                Predicate numberOfSharesPredicate = criteriaBuilder.equal(root.get("numberOfShares"), searchForm.getNumberOfShares());
                predicates.add(numberOfSharesPredicate);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

