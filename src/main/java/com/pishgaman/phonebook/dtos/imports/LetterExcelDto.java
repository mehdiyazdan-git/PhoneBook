package com.pishgaman.phonebook.dtos.imports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.enums.LetterState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Letter}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterExcelDto implements Serializable {
    private Long id;
    private String content;
    private String creationDate;
    private Long letterTypeId;
    private String letterNumber;
    private Integer customerCreatedBy;
    private Long companyId;
    private Long yearId;
    private LetterState letterState;
}