package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.CompanyDto;
import com.pishgaman.phonebook.dtos.CompanySelect;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.searchforms.CompanySearch;
import com.pishgaman.phonebook.services.CompanyService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            CompanySearch search) {
        Page<CompanyDto> companies = companyService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/download-all-companies.xlsx")
    public ResponseEntity<byte[]> downloadAllCompaniesExcel() throws IOException {
        byte[] excelData = companyService.exportCompaniesToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_companies.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import-companies")
    public ResponseEntity<String> importCompaniesFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = companyService.importCompaniesFromExcel(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import companies from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadCompanyTemplate() {
        try {
            byte[] templateBytes = companyService.generateCompanyTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "company_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    @GetMapping(path = "/select")
    public ResponseEntity<List<CompanySelect>> findAllCompanySelect(@RequestParam(required = false) String queryParam) {
        List<CompanySelect> companySelects = companyService.findAllCompanySelect(queryParam);
        return ResponseEntity.ok(companySelects);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<CompanySelect>> searchCompanyByNameContaining(@RequestParam(name = "searchQuery",required = false) String searchQuery) {
        List<CompanySelect> companies = companyService.searchCompanyByNameContaining(searchQuery);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long companyId) {
        CompanyDto company = companyService.findById(companyId);
        return ResponseEntity.ok(company);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto) {
        try {
            CompanyDto createdCompany = companyService.createCompany(companyDto);
            return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable("companyId") Long companyId, @RequestBody CompanyDto companyDto) {
        try {
            CompanyDto updatedCompany = companyService.updateCompany(companyId, companyDto);
            return ResponseEntity.ok(updatedCompany);
        } catch (EntityAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<String> deleteCompany(@PathVariable("companyId") Long companyId) {
        try {
            String message = companyService.removeCompany(companyId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
