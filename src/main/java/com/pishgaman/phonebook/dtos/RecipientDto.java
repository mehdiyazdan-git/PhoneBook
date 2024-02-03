package com.pishgaman.phonebook.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Recipient}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientDto implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String nationalIdentity;
    private String registerCode;
    private LocalDate registerDate;
}