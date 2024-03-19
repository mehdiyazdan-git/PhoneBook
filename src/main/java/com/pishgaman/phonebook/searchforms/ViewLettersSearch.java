package com.pishgaman.phonebook.searchforms;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.ViewLetters}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewLettersSearch implements Serializable {
    private Long letterId;
    private LocalDate creationDate;
    private String content;
    private String letterNumber;
    private String recipientName;
    private Long recipientId;
}