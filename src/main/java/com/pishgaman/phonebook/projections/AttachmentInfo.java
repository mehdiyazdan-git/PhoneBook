package com.pishgaman.phonebook.projections;

/**
 * Projection for {@link com.pishgaman.phonebook.entities.Attachment}
 */
public interface AttachmentInfo {
    Long getId();
    String getFileName();
    String getFileType();
    default String getFullName() {
        return getFileName().concat(".").concat(getFileType());
    }
}