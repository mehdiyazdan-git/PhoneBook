package com.pishgaman.phonebook.searchforms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InsuranceSlipSearchForm implements Serializable {
    private Long id;
    private String issueDate;
    private String slipNumber;
    private InsuranceSlip.SlipType type;
    private BigDecimal amount;
    private String startDate;
    private String endDate;
    private Long companyId;
    private String companyName;
}
