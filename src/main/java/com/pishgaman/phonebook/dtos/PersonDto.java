package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Person}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto  extends BaseDto  implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String nationalId;
    private LocalDate birthDate;
    private String registrationNumber;
    private String postalCode;
    private String address;
    private String phoneNumber;
}