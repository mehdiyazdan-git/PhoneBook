package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.Shareholder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Shareholder}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareholderDto implements Serializable {
    private Long id;
    private Long personId;
    private Integer numberOfShares;
    private BigDecimal percentageOwnership;
    private BigDecimal sharePrice;
    private Shareholder.ShareType shareType;
    private Long companyId;
    private byte[] scannedShareCertificate;
}