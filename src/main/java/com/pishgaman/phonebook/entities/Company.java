package com.pishgaman.phonebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer letterCounter;

    // Add this inside your Company class.

    @OneToMany(mappedBy = "company",orphanRemoval = true)
    private Set<BoardMember> boardMembers;

    @OneToMany(mappedBy = "company", orphanRemoval = true)
    private Set<Document> documents = new LinkedHashSet<>();

}
