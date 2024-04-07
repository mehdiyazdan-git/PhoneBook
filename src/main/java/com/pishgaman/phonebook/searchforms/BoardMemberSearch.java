package com.pishgaman.phonebook.searchforms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardMemberSearch {
    private Long id;
    private Long personId;
    private String personFirstName;
    private String personLastName;
    private String fullName;
    private String companyCompanyName;
    private Long companyId;
    private String positionName;
}
