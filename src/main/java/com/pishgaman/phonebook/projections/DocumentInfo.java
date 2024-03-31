package com.pishgaman.phonebook.projections;

/**
 * Projection for {@link com.pishgaman.phonebook.entities.Document}
 */
public interface DocumentInfo {
    Long getId();

    String getDocumentName();

    String getDocumentType();

    String getFileExtension();
}