package com.pishgaman.phonebook.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.services.DocumentService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @Data
    public static class FormDataWithFile {
        private String documentName;
        private String documentType;
        private String fileExtension;
        private Long personId;
        private Long companyId;
        private Long letterId;
    }

    @PostMapping(path = { "/", "" },consumes = "multipart/form-data")
    public ResponseEntity<DocumentDto> createDocument(
            @RequestParam("documentFile") MultipartFile documentFile,
            @ModelAttribute FormDataWithFile formData
    ) {
        if (documentFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        DocumentDto documentDto = new DocumentDto();
        documentDto.setDocumentName(formData.getDocumentName());
        documentDto.setDocumentType(formData.getDocumentType());
        documentDto.setFileExtension(formData.getFileExtension());
        try {
            documentDto.setDocumentFile(documentFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if (formData.getPersonId() != null && formData.getPersonId() instanceof Long) {
            documentDto.setPersonId(formData.getPersonId());
        }
        if (formData.getCompanyId() != null && formData.getCompanyId() instanceof Long) {
            documentDto.setCompanyId(formData.getCompanyId());
        }
        if (formData.getLetterId() != null && formData.getLetterId() instanceof Long) {
            documentDto.setLetterId(formData.getLetterId());
        }

        DocumentDto newDocument = documentService.createDocument(documentDto);
        return ResponseEntity.ok(newDocument);
    }

    @GetMapping(path = "/by-person-id/{personId}")
    public ResponseEntity<List<DocumentDetailDto>> getAllDocuments(@PathVariable Long personId) {
        try {
            List<DocumentDetailDto> documents = documentService.findAllByPersonId(personId);
            return ResponseEntity.ok(documents);
        }catch (EntityNotFoundException e){
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping(path = "/by-company-id/{companyId}")
    public ResponseEntity<List<DocumentDetailDto>> findAllByCompanyId(@PathVariable Long companyId) {
        try {
            List<DocumentDetailDto> documents = documentService.findAllByCompanyId(companyId);
            return ResponseEntity.ok(documents);
        }catch (EntityNotFoundException e){
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/by-letter-id/{letterId}")
    public ResponseEntity<List<DocumentDetailDto>> findAllByLetterId(@PathVariable Long letterId) {
      try {
          List<DocumentDetailDto> documents = documentService.findAllByLetterId(letterId);
          return ResponseEntity.ok(documents);
      }catch (EntityNotFoundException e){
          e.printStackTrace();
          return ResponseEntity.notFound().build();
      }
    }

    @GetMapping("/download-all-documents.xlsx")
    public ResponseEntity<byte[]> downloadAllDocumentsExcel() throws IOException {
        byte[] excelData = documentService.generateDocumentTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_documents.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import-documents")
    public ResponseEntity<String> importDocumentsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = documentService.importDocumentsFromExcel(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import documents from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadDocumentSlipTemplate() {
        try {
            byte[] templateBytes = documentService.generateDocumentTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "document_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
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
      try {
          documentService.deleteDocument(id);
          return ResponseEntity.noContent().build();
      }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }
}
