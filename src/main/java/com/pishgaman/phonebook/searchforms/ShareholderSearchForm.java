package com.pishgaman.phonebook.searchforms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareholderSearchForm {
    private String personName;
    private String companyName;
    private String shareType;
    private Long companyId;
}
