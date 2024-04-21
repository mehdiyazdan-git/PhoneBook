package com.pishgaman.phonebook.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Attachment}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDto  extends BaseDto  implements Serializable {
    private Long id;
    private String fileName;
    private String fileType;
    private byte[] fileContent;
    private Long letterId;
    @Column(name = "deletable")
    private boolean deletable = true;
}