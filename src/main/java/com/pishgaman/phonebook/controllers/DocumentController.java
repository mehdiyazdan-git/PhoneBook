package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.services.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(path = { "/", "" })
    public ResponseEntity<DocumentDto> createDocument(
            @RequestParam("documentName") String documentName,
            @RequestParam("documentType") String documentType,
            @RequestParam("fileExtension") String fileExtension,
            @RequestParam("documentFile") MultipartFile documentFile,
            @RequestParam(value = "personId",required = false) Long personId,
            @RequestParam(value = "companyId",required = false) Long companyId
            ) {
        if (documentFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        DocumentDto documentDto = new DocumentDto();
        documentDto.setDocumentName(documentName);
        documentDto.setDocumentType(documentType);
        documentDto.setFileExtension(fileExtension);
        try {
            documentDto.setDocumentFile(documentFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        if (personId != null && personId instanceof Long){
            documentDto.setPersonId(personId);
        }
        if (companyId != null && companyId instanceof Long){
            documentDto.setCompanyId(companyId);
        }
        DocumentDto newDocument = documentService.createDocument(documentDto);
        return ResponseEntity.ok(newDocument);
    }

    @GetMapping(path = "/by-person-id/{personId}")
    public ResponseEntity<List<DocumentDetailDto>> getAllDocuments(@PathVariable Long personId) {
        List<DocumentDetailDto> documents = documentService.findAllByPersonId(personId);
        return ResponseEntity.ok(documents);
    }
    @GetMapping(path = "/by-company-id/{companyId}")
    public ResponseEntity<List<DocumentDetailDto>> findAllByCompanyId(@PathVariable Long companyId) {
        List<DocumentDetailDto> documents = documentService.findAllByCompanyId(companyId);
        return ResponseEntity.ok(documents);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Resource> getDocumentById(@PathVariable Long id) {
        DocumentDto document = documentService.getDocumentById(id);
        if (document == null || document.getDocumentFile() == null) {
            return ResponseEntity.notFound().build();
        }
        String fileExtension = document.getFileExtension().toLowerCase();
        MediaType mediaType = switch (fileExtension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "docx", "doc" ->
                    MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xlsx", "xls" ->
                    MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "pptx", "ppt" ->
                    MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            case "jpg", "jpeg", "png", "gif", "bmp" -> MediaType.IMAGE_JPEG;
            case "csv" -> MediaType.valueOf("text/csv");
            case "tif" -> MediaType.valueOf("image/tiff");
            case "txt" -> MediaType.valueOf("text/plain");
            case "zip" -> MediaType.valueOf("application/zip");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        ByteArrayResource resource = new ByteArrayResource(document.getDocumentFile());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getDocumentName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
