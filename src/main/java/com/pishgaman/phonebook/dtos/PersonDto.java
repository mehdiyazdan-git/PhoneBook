package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Person}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private Long recipientId;
}