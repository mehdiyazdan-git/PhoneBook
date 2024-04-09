package com.pishgaman.phonebook.specifications;

import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.searchforms.CompanySearch;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CompanySpecification {

    public static Specification<Company> getSpecification(CompanySearch search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search.getTaxEconomicCode())) {
                predicates.add(cb.like(root.get("taxEconomicCode"), "%" + search.getTaxEconomicCode().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxFileNumber())) {
                predicates.add(cb.like(root.get("taxFileNumber"), "%" + search.getTaxFileNumber().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxFileClass())) {
                predicates.add(cb.like(root.get("taxFileClass"), "%" + search.getTaxFileClass().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxTrackingID())) {
                predicates.add(cb.like(root.get("taxTrackingID"), "%" + search.getTaxTrackingID().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxPortalUsername())) {
                predicates.add(cb.like(root.get("taxPortalUsername"), "%" + search.getTaxPortalUsername().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxPortalPassword())) {
                predicates.add(cb.like(root.get("taxPortalPassword"), "%" + search.getTaxPortalPassword().trim() + "%"));
            }
            if (StringUtils.hasText(search.getTaxDepartment())) {
                predicates.add(cb.like(root.get("taxDepartment"), "%" + search.getTaxDepartment().trim() + "%"));
            }
            if (StringUtils.hasText(search.getCompanyName())) {
                predicates.add(cb.like(root.get("companyName"), "%" + search.getCompanyName().trim() + "%"));
            }
            if (StringUtils.hasText(search.getNationalId())) {
                predicates.add(cb.like(root.get("nationalId"), "%" + search.getNationalId().trim() + "%"));
            }
            if (StringUtils.hasText(search.getRegistrationNumber())) {
                predicates.add(cb.like(root.get("registrationNumber"), "%" + search.getRegistrationNumber().trim() + "%"));
            }
            if (search.getRegistrationDate() != null && !search.getRegistrationDate().isEmpty()) {
                LocalDate registrationDate = LocalDate.parse(search.getRegistrationDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                predicates.add(cb.equal(root.get("registrationDate"), registrationDate));
            }
            if (search.getRegistrationDate() != null && !search.getRegistrationDate().isEmpty()) {
                String trimmed = search.getRegistrationDate().trim();
                String[] dateParts = trimmed.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                LocalDate date = LocalDate.of(year, month, day);
                predicates.add(cb.equal(root.get("registrationDate"), date));
            }
            if (StringUtils.hasText(search.getAddress())) {
                predicates.add(cb.like(root.get("address"), "%" + search.getAddress().trim() + "%"));
            }
            if (StringUtils.hasText(search.getPostalCode())) {
                predicates.add(cb.like(root.get("postalCode"), "%" + search.getPostalCode().trim() + "%"));
            }
            if (StringUtils.hasText(search.getPhoneNumber())) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + search.getPhoneNumber().trim() + "%"));
            }
            if (StringUtils.hasText(search.getFaxNumber())) {
                predicates.add(cb.like(root.get("faxNumber"), "%" + search.getFaxNumber().trim() + "%"));
            }
            if (StringUtils.hasText(search.getSoftwareUsername())) {
                predicates.add(cb.like(root.get("softwareUsername"), "%" + search.getSoftwareUsername().trim() + "%"));
            }
            if (StringUtils.hasText(search.getSoftwarePassword())) {
                predicates.add(cb.like(root.get("softwarePassword"), "%" + search.getSoftwarePassword().trim() + "%"));
            }
            if (StringUtils.hasText(search.getSoftwareCallCenter())) {
                predicates.add(cb.like(root.get("softwareCallCenter"), "%" + search.getSoftwareCallCenter().trim() + "%"));
            }
            if (StringUtils.hasText(search.getInsurancePortalUsername())) {
                predicates.add(cb.like(root.get("insurancePortalUsername"), "%" + search.getInsurancePortalUsername().trim() + "%"));
            }
            if (StringUtils.hasText(search.getInsurancePortalPassword())) {
                predicates.add(cb.like(root.get("insurancePortalPassword"), "%" + search.getInsurancePortalPassword().trim() + "%"));
            }
            if (StringUtils.hasText(search.getInsuranceBranch())) {
                predicates.add(cb.like(root.get("insuranceBranch"), "%" + search.getInsuranceBranch().trim() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Company> getSelectSpecification(String searchParam){
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (searchParam !=null && !searchParam.isEmpty()) {
                predicate =(cb.like(root.get("companyName"), "%" + searchParam.trim() + "%"));
            }
            return predicate;
        };

    }
}
