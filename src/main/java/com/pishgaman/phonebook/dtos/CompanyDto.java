package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Company}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto  extends BaseDto  implements Serializable {
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
    private LocalDate registrationDate;
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
    private String letterPrefix;
}