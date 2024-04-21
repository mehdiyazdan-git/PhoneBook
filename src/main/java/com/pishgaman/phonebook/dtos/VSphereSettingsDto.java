package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VSphereSettingsDto implements Serializable {
    private String vsphereUrl;
    private String vsphereUsername;
    private String vspherePassword;
}
