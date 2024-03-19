package com.pishgaman.phonebook.searchforms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSearch {
    private Long id;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String nationalId;
    private String birthDate;
    private String registrationNumber;
    private String postalCode;
    private String address;
}
