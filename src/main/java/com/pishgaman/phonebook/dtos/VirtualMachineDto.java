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
public class VirtualMachineDto implements Serializable {
    private String name;
    private String ipAddress;
    private String guestOsFullName;
    private String guestHostName;
    private String notes;
}
