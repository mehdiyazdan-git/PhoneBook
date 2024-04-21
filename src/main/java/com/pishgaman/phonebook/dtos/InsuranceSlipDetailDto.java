package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.InsuranceSlip}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceSlipDetailDto  extends BaseDto  implements Serializable {
    private Long id;
    private LocalDate issueDate;
    private String slipNumber;
    private String type;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long companyId;
    private String companyCompanyName;
    private String fileName;
}