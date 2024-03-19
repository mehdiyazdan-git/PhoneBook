package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PersonDocumentDto;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.PersonDocument;
import com.pishgaman.phonebook.mappers.PersonDocumentMapper;
import com.pishgaman.phonebook.repositories.PersonDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonDocumentService {
    private final PersonDocumentRepository personDocumentRepository;
    private final PersonDocumentMapper personDocumentMapper;

    // Method to handle individual file uploads
    public PersonDocumentDto uploadDocument(Long personId, String documentType, MultipartFile file) throws IOException {
        // Find existing or create new PersonDocument
        Optional<PersonDocument> document = personDocumentRepository.findByPersonId(personId);
            PersonDocument personDocument = new PersonDocument();
        if (document.isEmpty()){

        personDocument.setPerson( new Person( personId ) );
        personDocument.setId( null);
        personDocument.setDocumentName( documentType );
        personDocument.setDocumentType( documentType );
        }

        // Set the file for the appropriate document type
        switch (documentType) {
            case "nationalIdFile" -> personDocument.setNationalIdFile(file.getBytes());
            case "birthCertificateFile" -> personDocument.setBirthCertificateFile(file.getBytes());
            case "cardServiceFile" -> personDocument.setCardServiceFile(file.getBytes());
            case "academicDegreeFile" -> personDocument.setAcademicDegreeFile(file.getBytes());
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        }

        // Save the updated personDocument
        personDocument = personDocumentRepository.save(personDocument);

        // Return the updated DTO
        return personDocumentMapper.toDto(personDocument);
    }

    public PersonDocumentDto findByPersonId(Long personId) {
        Optional<PersonDocument> personDocument = personDocumentRepository.findByPersonId(personId);
        return personDocument.map(personDocumentMapper::toDto).orElse(null);
    }

    // Method to handle individual file deletions
    public void deleteDocument(Long personId, String documentType) {
        PersonDocument personDocument = personDocumentRepository.findByPersonId(personId)
                .orElseThrow(() -> new IllegalArgumentException("PersonDocument not found with id: " + personId));

        // Clear the content for the appropriate document type
        switch (documentType) {
            case "nationalIdFile" -> personDocument.setNationalIdFile(null);
            case "birthCertificateFile" -> personDocument.setBirthCertificateFile(null);
            case "cardServiceFile" -> personDocument.setCardServiceFile(null);
            case "academicDegreeFile" -> personDocument.setAcademicDegreeFile(null);
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        }

        // Save the updated personDocument
        personDocumentRepository.save(personDocument);
    }

    // Additional methods...
}
