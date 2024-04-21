package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDto implements Serializable {
    private Integer createdBy;
    private Integer lastModifiedBy;
    private LocalDateTime createdDate;
    private String createAtJalali;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedAtJalali;
    private String createByFullName;
    private String lastModifiedByFullName;
}
