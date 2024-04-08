package com.pishgaman.phonebook.searchforms;

import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class TaxPaymentSlipSearchForm
{
    private Long id;
    private LocalDate issueDate;
    private String slipNumber;
    private TaxPaymentSlip.TaxPaymentSlipType type;
    private BigDecimal amount;
    private String period;
    private Long companyId;
    private String companyName;
}
