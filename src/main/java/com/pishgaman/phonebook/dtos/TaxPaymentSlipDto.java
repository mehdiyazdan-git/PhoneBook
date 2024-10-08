package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.TaxPaymentSlip}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxPaymentSlipDto extends BaseDto implements Serializable {
    private Long id;
    private LocalDate issueDate;
    private String slipNumber;
    private TaxPaymentSlip.TaxPaymentSlipType type;
    private BigDecimal amount;
    private String period;
    private Long companyId;
    private String companyCompanyName;
    private byte[] file;
    private String fileExtension;
    private String fileName;
}