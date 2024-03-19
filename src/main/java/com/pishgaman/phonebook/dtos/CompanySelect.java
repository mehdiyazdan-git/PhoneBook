package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanySelect {
    private Long id;
    private String name;
    private String letterPrefix;
    private Integer letterCounter;

}
