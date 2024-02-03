package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Attachment}
 */
@Data
@NoArgsConstructor
public class AttachListDto implements Serializable {
    private Long id;
    private String fileName;
    private String fileType;
    private boolean deletable;


    public AttachListDto(Long id, String fileName, String fileType, boolean deletable) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.deletable = deletable;
    }
}