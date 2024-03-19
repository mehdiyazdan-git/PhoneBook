package com.pishgaman.phonebook.searchforms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerSearch {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String nationalIdentity;
    private String registerCode;
    private String registerDate;
}
