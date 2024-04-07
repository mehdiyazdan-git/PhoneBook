package com.pishgaman.phonebook.searchforms;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LetterSearch {
        private Long Id;
        private String creationDate;
        private String content;
        private String letterNumber;
        private String customerName;
        private String companyName;
        private Long customerId;
        private Long companyId;
        private String letterType;
        private Long yearId;
}
