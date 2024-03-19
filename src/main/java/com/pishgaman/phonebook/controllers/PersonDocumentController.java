package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.PersonDocumentDto;
import com.pishgaman.phonebook.services.PersonDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/person-documents")
public class PersonDocumentController {

    private final PersonDocumentService personDocumentService;

    @Autowired
    public PersonDocumentController(PersonDocumentService personDocumentService) {
        this.personDocumentService = personDocumentService;
    }

    // Handle uploading for individual document types
    @PostMapping("/{personId}/{documentType}")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long personId,
            @PathVariable String documentType,
            @RequestParam("file") MultipartFile file) throws IOException {

        // The service will need to handle different document types
        PersonDocumentDto updatedPersonDocument = personDocumentService.uploadDocument(
                personId, documentType, file);
        return ResponseEntity.ok(updatedPersonDocument);
    }

    @GetMapping("/{personId}")
    public ResponseEntity<PersonDocumentDto> getPersonDocument(@PathVariable Long personId) {
        PersonDocumentDto personDocumentDto = personDocumentService.findByPersonId(personId);
        return ResponseEntity.of(Optional.ofNullable(personDocumentDto));
    }

    // Handle deleting for individual document types
    @DeleteMapping("/{personId}/{documentType}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable Long personId,
            @PathVariable String documentType) {

        // The service will need to handle different document types
        personDocumentService.deleteDocument(personId, documentType);
        return ResponseEntity.noContent().build();
    }
}
