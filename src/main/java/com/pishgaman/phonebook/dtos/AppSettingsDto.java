package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.AppSettings}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSettingsDto implements Serializable {
    private Long id;
    private long maxUploadFileSize;
    private String vsphereUrl;
    private String vsphereUsername;
    private String vspherePassword;
    private String backupPath;
    private String databaseName;
}