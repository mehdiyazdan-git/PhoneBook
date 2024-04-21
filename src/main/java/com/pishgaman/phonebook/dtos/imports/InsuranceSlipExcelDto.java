package com.pishgaman.phonebook.dtos.imports;

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
public class InsuranceSlipExcelDto implements Serializable {
    private Long id;
    private String issueDate;
    private String slipNumber;
    private InsuranceSlip.SlipType type;
    private BigDecimal amount;
    private String startDate;
    private String endDate;
    private Long companyId;
}