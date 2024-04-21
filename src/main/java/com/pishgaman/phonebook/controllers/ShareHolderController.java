package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.ShareholderDetailDto;
import com.pishgaman.phonebook.dtos.ShareholderDto;
import com.pishgaman.phonebook.searchforms.ShareholderSearchForm;
import com.pishgaman.phonebook.services.ShareHolderService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/shareholders")
@RequiredArgsConstructor
public class ShareHolderController {
    private final ShareHolderService shareHolderService;

    @PostMapping("/import")
    public ResponseEntity<String> importShareHoldersFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = shareHolderService.uploadFromExcelFile(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportShareHoldersToExcel() throws IOException {
        byte[] excelData = shareHolderService.exportToExcelFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_shareholders.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @GetMapping("/download-all-shareholders.xlsx")
    public ResponseEntity<byte[]> downloadAllShareHoldersExcel() throws IOException {
        byte[] excelData = shareHolderService.exportShareHoldersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_shareholders.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }


    @GetMapping
    public ResponseEntity<Page<ShareholderDetailDto>> getAllShareHolders(
            ShareholderSearchForm search,
            @RequestParam Optional<Long> companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {
        companyId.ifPresent(search::setCompanyId);
        Page<ShareholderDetailDto> shareholders = shareHolderService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(shareholders);
    }

    @GetMapping("/{shareHolderId}")
    public ResponseEntity<ShareholderDto> getShareHolderById(@PathVariable Long shareHolderId) {
        ShareholderDto shareholder = shareHolderService.findById(shareHolderId);
        return ResponseEntity.ok(shareholder);
    }

    @PostMapping
    public ResponseEntity<ShareholderDto> createShareHolder(@RequestBody ShareholderDto shareholderDto) {
        ShareholderDto createdShareholder = shareHolderService.createShareHolder(shareholderDto);
        return new ResponseEntity<>(createdShareholder, HttpStatus.CREATED);
    }

    @PostMapping("/{shareHolderId}/upload-file")
    public ResponseEntity<String> uploadShareHolderFile(
            @PathVariable("shareHolderId") Long shareHolderId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileExtension") String fileExtension,
            @RequestParam("fileName") String fileName
    ) {
        try {
            shareHolderService.saveShareholderFile(shareHolderId, file, fileName, fileExtension);
            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file: " + e.getMessage());
        }
    }
    @GetMapping("/{shareHolderId}/download-file")
    public ResponseEntity<Resource> downloadShareHolderFile(@PathVariable Long shareHolderId) {
        ShareholderDto shareholder = shareHolderService.findById(shareHolderId);

        byte[] fileData = shareholder.getScannedShareCertificate();
        String fileExtension = shareholder.getFileExtension().toLowerCase();
        MediaType mediaType = switch (fileExtension.toLowerCase()) {
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

        if (fileData == null) {
            throw new EntityNotFoundException("No file found for shareholder with id: " + shareHolderId);
        }
        String fileName = "certificate_" + shareHolderId + "." + fileExtension;
        ByteArrayResource resource = new ByteArrayResource(shareholder.getScannedShareCertificate());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadShareholderTemplate() {
        try {
            byte[] templateBytes = shareHolderService.generateShareholderTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "shareholders_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }


    @PutMapping("/{shareHolderId}")
    public ResponseEntity<ShareholderDto> updateShareHolder(@PathVariable("shareHolderId") Long shareHolderId, @RequestBody ShareholderDto shareholderDto) {
        ShareholderDto updatedShareholder = shareHolderService.updateShareHolder(shareHolderId, shareholderDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedShareholder);
    }

    @DeleteMapping("/{shareHolderId}")
    public ResponseEntity<String> deleteShareHolder(@PathVariable("shareHolderId") Long shareHolderId) {
        try {
            String message = shareHolderService.removeShareHolder(shareHolderId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{shareholderId}/delete-file")
    public ResponseEntity<String> deleteShareholderFile(
            @PathVariable("shareholderId") Long shareholderId
    ) {
        try {
            String message = shareHolderService.deleteShareholderFile(shareholderId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
