package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Year}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearDto implements Serializable {
    private Long id;
    private Long name;
    private Long startingLetterNumber;
}