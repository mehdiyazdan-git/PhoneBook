package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.ShareholderDetailDto;
import com.pishgaman.phonebook.dtos.ShareholderDto;
import com.pishgaman.phonebook.entities.Shareholder;
import com.pishgaman.phonebook.searchforms.ShareholderSearchForm;
import com.pishgaman.phonebook.services.ShareHolderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @GetMapping
    public ResponseEntity<Page<ShareholderDetailDto>> getAllShareHolders(
            ShareholderSearchForm search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {
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
            @PathVariable Long shareHolderId,
            @RequestParam("file") MultipartFile file) {
        try {
            shareHolderService.saveShareHolderFile(shareHolderId, file);
            return ResponseEntity.ok("File uploaded successfully.");
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
    public ResponseEntity<byte[]> downloadShareHolderFile(@PathVariable Long shareHolderId) {
        ShareholderDto shareholder = shareHolderService.findById(shareHolderId);

        byte[] fileData = shareholder.getScannedShareCertificate();

        if (fileData == null) {
            throw new EntityNotFoundException("No file found for shareholder with id: " + shareHolderId);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "file" )
                .body(fileData);
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
}
