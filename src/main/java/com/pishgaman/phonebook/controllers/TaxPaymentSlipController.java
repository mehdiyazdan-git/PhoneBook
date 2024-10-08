package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.TaxPaymentSlipDetailDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.searchforms.TaxPaymentSlipSearchForm;
import com.pishgaman.phonebook.services.TaxPaymentSlipService;
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
@RequestMapping(path = "/api/tax-payment-slips")
@RequiredArgsConstructor
public class TaxPaymentSlipController {
    private final TaxPaymentSlipService taxPaymentSlipService;

    @PostMapping("/import")
    public ResponseEntity<String> importTaxPaymentSlipsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = taxPaymentSlipService.uploadFromExcelFile(file);
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

    @GetMapping("/export/{companyId}")
    public ResponseEntity<byte[]> exportTaxPaymentSlipsToExcel(@PathVariable("companyId") Long companyId) throws IOException {
        byte[] excelData = taxPaymentSlipService.exportToExcelFile(companyId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_tax_payment_slips.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTaxPaymentSlipTemplate() {
        try {
            byte[] templateBytes = taxPaymentSlipService.generateTaxPaymentSlipTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "tax_payment_slip_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }


    @GetMapping
    public ResponseEntity<Page<TaxPaymentSlipDetailDto>> getAllTaxPaymentSlips(
            TaxPaymentSlipSearchForm search,
            @RequestParam Optional<Long> companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {
        companyId.ifPresent(search::setCompanyId);
        Page<TaxPaymentSlipDetailDto> taxPaymentSlips = taxPaymentSlipService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(taxPaymentSlips);
    }

    @GetMapping("/{taxPaymentSlipId}")
    public ResponseEntity<TaxPaymentSlipDto> getTaxPaymentSlipById(@PathVariable Long taxPaymentSlipId) {
        TaxPaymentSlipDto taxPaymentSlip = taxPaymentSlipService.findById(taxPaymentSlipId);
        return ResponseEntity.ok(taxPaymentSlip);
    }

    @PostMapping
    public ResponseEntity<TaxPaymentSlipDto> createTaxPaymentSlip(@RequestBody TaxPaymentSlipDto taxPaymentSlipDto) {
        TaxPaymentSlipDto createdTaxPaymentSlip = taxPaymentSlipService.createTaxPaymentSlip(taxPaymentSlipDto);
        return new ResponseEntity<>(createdTaxPaymentSlip, HttpStatus.CREATED);
    }


    @PostMapping("/{taxPaymentSlipId}/upload-file")
    public ResponseEntity<String> uploadTaxPaymentSlipFile(
            @PathVariable("taxPaymentSlipId") Long taxPaymentSlipId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileExtension") String fileExtension,
            @RequestParam("fileName") String fileName
    ) {
        try {
            taxPaymentSlipService.saveTaxPaymentSlipFile(taxPaymentSlipId, file,fileName,fileExtension);
            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
        } catch (IOException e) {
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

    @GetMapping("/{taxPaymentSlipId}/download-file")
    public ResponseEntity<Resource> downloadTaxPaymentSlipFile(@PathVariable Long taxPaymentSlipId) {
        TaxPaymentSlipDto taxPaymentSlip = taxPaymentSlipService.findById(taxPaymentSlipId);

        byte[] fileData = taxPaymentSlip.getFile();
        String fileExtension = taxPaymentSlip.getFileExtension().toLowerCase();
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
            throw new EntityNotFoundException("No file found for tax payment slip with id: " + taxPaymentSlipId);
        }
        String fileName = "certificate_" + taxPaymentSlipId + "." + fileExtension;
        ByteArrayResource resource = new ByteArrayResource(taxPaymentSlip.getFile());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/download-all-taxpaymentslips.xlsx")
    public ResponseEntity<byte[]> downloadAllTaxPaymentSlipsExcel() throws IOException {
        byte[] excelData = taxPaymentSlipService.exportTaxPaymentSlipsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_taxpaymentslips.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }


    @PutMapping("/{taxPaymentSlipId}")
    public ResponseEntity<TaxPaymentSlipDto> updateTaxPaymentSlip(
            @PathVariable("taxPaymentSlipId") Long taxPaymentSlipId,
            @RequestBody TaxPaymentSlipDto taxPaymentSlipDto) {
        TaxPaymentSlipDto updatedTaxPaymentSlip =
                taxPaymentSlipService.updateTaxPaymentSlip(taxPaymentSlipId, taxPaymentSlipDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTaxPaymentSlip);
    }

    @DeleteMapping("/{taxPaymentSlipId}")
    public ResponseEntity<String> deleteTaxPaymentSlip(
            @PathVariable("taxPaymentSlipId") Long taxPaymentSlipId
    ) {
        try {
            String message = taxPaymentSlipService.removeTaxPaymentSlip(taxPaymentSlipId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{taxPaymentSlipId}/delete-file")
    public ResponseEntity<String> deleteTaxPaymentSlipFile(
            @PathVariable("taxPaymentSlipId") Long taxPaymentSlipId
    ) {
        try {
            String message = taxPaymentSlipService.deleteTaxPaymentSlipFile(taxPaymentSlipId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

