package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.PersonDocument}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDocumentDto implements Serializable {
    private Long id;
    private String documentName;
    private String documentType;
    private byte[] nationalIdFile;
    private byte[] birthCertificateFile;
    private byte[] cardServiceFile;
    private byte[] academicDegreeFile;
    private String fileExtension;
    private Long personId;
}