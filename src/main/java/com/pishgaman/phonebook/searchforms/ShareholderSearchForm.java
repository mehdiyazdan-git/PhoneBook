package com.pishgaman.phonebook.searchforms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.entities.Shareholder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareholderSearchForm {
    private Long id;
    private String personFirstName;
    private String personLastName;
    private Long companyId;
    private String companyName;
    private Shareholder.ShareType shareType;
    private Integer numberOfShares;
    private BigDecimal percentageOwnership;
    private BigDecimal sharePrice;
}
