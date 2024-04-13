package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.InsuranceSlipDetailDto;
import com.pishgaman.phonebook.dtos.InsuranceSlipDto;
import com.pishgaman.phonebook.searchforms.InsuranceSlipSearchForm;
import com.pishgaman.phonebook.services.InsuranceSlipService;
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
@RequestMapping(path = "/api/insurance-slips")
@RequiredArgsConstructor
public class InsuranceSlipController {
    private final InsuranceSlipService insuranceSlipService;

    @PostMapping("/import")
    public ResponseEntity<String> importInsuranceSlipsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = insuranceSlipService.uploadFromExcelFile(file);
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
    public ResponseEntity<byte[]> exportInsuranceSlipsToExcel() throws IOException {
        byte[] excelData = insuranceSlipService.exportToExcelFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_insurance_slips.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @GetMapping
    public ResponseEntity<Page<InsuranceSlipDetailDto>> getAllInsuranceSlips(
            InsuranceSlipSearchForm search,
            @RequestParam Optional<Long> companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order) {
        companyId.ifPresent(search::setCompanyId);
        Page<InsuranceSlipDetailDto> insuranceSlips = insuranceSlipService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(insuranceSlips);
    }

    @GetMapping("/{insuranceSlipId}")
    public ResponseEntity<InsuranceSlipDto> getInsuranceSlipById(@PathVariable Long insuranceSlipId) {
        InsuranceSlipDto insuranceSlip = insuranceSlipService.findById(insuranceSlipId);
        return ResponseEntity.ok(insuranceSlip);
    }

    @PostMapping
    public ResponseEntity<InsuranceSlipDto> createInsuranceSlip(@RequestBody InsuranceSlipDto insuranceSlipDto) {
        InsuranceSlipDto createdInsuranceSlip = insuranceSlipService.createInsuranceSlip(insuranceSlipDto);
        return new ResponseEntity<>(createdInsuranceSlip, HttpStatus.CREATED);
    }

    @GetMapping("/download-all-insuranceslips.xlsx")
    public ResponseEntity<byte[]> downloadAllInsuranceSlipsExcel() throws IOException {
        byte[] excelData = insuranceSlipService.exportInsuranceSlipsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_insuranceslips.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }


    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadInsuranceSlipTemplate() {
        try {
            byte[] templateBytes = insuranceSlipService.generateInsuranceSlipTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "insurance_slip_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @CrossOrigin(
            origins = "http://localhost:3000",
            methods = {RequestMethod.GET, RequestMethod.POST},
            allowedHeaders = "*")
    @PostMapping("/{insuranceSlipId}/upload-file")
    public ResponseEntity<String> uploadInsuranceSlipFile(
            @PathVariable("insuranceSlipId") Long insuranceSlipId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileExtension") String fileExtension,
            @RequestParam("fileName") String fileName
    ) {
        try {
            insuranceSlipService.saveInsuranceSlipFile(insuranceSlipId, file, fileName, fileExtension);
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

    @GetMapping("/{insuranceSlipId}/download-file")
    public ResponseEntity<Resource> downloadInsuranceSlipFile(@PathVariable Long insuranceSlipId) {
        InsuranceSlipDto insuranceSlip = insuranceSlipService.findById(insuranceSlipId);

        byte[] fileData = insuranceSlip.getFile();
        String fileExtension = insuranceSlip.getFileName().toLowerCase();
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
            throw new EntityNotFoundException("No file found for insurance slip with id: " + insuranceSlipId);
        }
        String fileName = "certificate_" + insuranceSlipId + "." + fileExtension;
        ByteArrayResource resource = new ByteArrayResource(insuranceSlip.getFile());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PutMapping("/{insuranceSlipId}")
    public ResponseEntity<InsuranceSlipDto> updateInsuranceSlip(@PathVariable("insuranceSlipId") Long insuranceSlipId, @RequestBody InsuranceSlipDto insuranceSlipDto) {
        InsuranceSlipDto updatedInsuranceSlip = insuranceSlipService.updateInsuranceSlip(insuranceSlipId, insuranceSlipDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedInsuranceSlip);
    }

    @DeleteMapping("/{insuranceSlipId}")
    public ResponseEntity<String> deleteInsuranceSlip(@PathVariable("insuranceSlipId") Long insuranceSlipId) {
        try {
            String message = insuranceSlipService.removeInsuranceSlip(insuranceSlipId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{insuranceSlipId}/delete-file")
    public ResponseEntity<String> deleteInsuranceSlipFile(
            @PathVariable("insuranceSlipId") Long insuranceSlipId
    ) {
        try {
            String message = insuranceSlipService.deleteInsuranceSlipFile(insuranceSlipId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
