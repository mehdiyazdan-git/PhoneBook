package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BackupFileDetails {
    private String fileName;
    private String filePath;
    private String fileSize;
    private String fileType;
    private String fileCreatedDate;
}
