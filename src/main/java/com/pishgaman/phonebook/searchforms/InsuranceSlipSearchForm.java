package com.pishgaman.phonebook.searchforms;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class InsuranceSlipSearchForm {
    private Long id;
    private LocalDate issueDate;
    private String slipNumber;
    private InsuranceSlip.SlipType type;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String companyName;
}
