package com.pishgaman.phonebook.searchforms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SenderSearch {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
}
