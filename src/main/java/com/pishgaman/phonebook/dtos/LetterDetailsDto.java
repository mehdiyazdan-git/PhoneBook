package com.pishgaman.phonebook.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
public class LetterDetailsDto implements Serializable {
    private Long id;
    private String content;
    private LocalDate creationDate;
    private String letterNumber;
    private Long customerId;
    private String customerName;
    private Long companyId;
    private String companyName;
    private Long yearId;


    public LetterDetailsDto(Long id,
                            String content,
                            LocalDate creationDate,
                            String letterNumber,
                            Long customerId,
                            String customerName,
                            Long companyId,
                            String companyName,
                            Long yearId
    ) {
        this.id = id;
        this.content = content;
        this.creationDate = creationDate;
        this.letterNumber = letterNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.yearId = yearId;
    }
}



// signature [
// java.lang.Long,
// java.lang.String,
// java.time.LocalDate,
// java.lang.String,
// java.lang.Long,
// java.lang.String
// ]
