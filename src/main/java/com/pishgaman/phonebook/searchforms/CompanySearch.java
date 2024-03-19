package com.pishgaman.phonebook.searchforms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanySearch {
    private Long id;
    private String taxEconomicCode;
    private String taxFileNumber;
    private String taxFileClass;
    private String taxTrackingID;
    private String taxPortalUsername;
    private String taxPortalPassword;
    private String taxDepartment;
    private String companyName;
    private String nationalId;
    private String registrationNumber;
    private String registrationDate;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String faxNumber;
    private String softwareUsername;
    private String softwarePassword;
    private String softwareCallCenter;
    private String insurancePortalUsername;
    private String insurancePortalPassword;
    private String insuranceBranch;
}
