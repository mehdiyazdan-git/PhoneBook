package com.pishgaman.phonebook.searchforms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TaxPaymentSlipSearchForm implements Serializable {
    private Long id;
    private String issueDate;
    private String slipNumber;
    private TaxPaymentSlip.TaxPaymentSlipType type;
    private BigDecimal amount;
    private String period;
    private Long companyId;
    private String companyName;
}
