package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Document}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDto  extends BaseDto  implements Serializable {
    private Long id;
    private String documentName;
    private String documentType;
    private String fileExtension;
    private byte[] documentFile;
    private Long personId;
    private Long companyId;
    private Long letterId;
}