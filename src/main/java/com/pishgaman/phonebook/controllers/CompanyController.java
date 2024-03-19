package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.CompanyDto;
import com.pishgaman.phonebook.dtos.CompanySelect;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.searchforms.CompanySearch;
import com.pishgaman.phonebook.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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
    @GetMapping(path = "/select")
    public ResponseEntity<List<CompanySelect>> findAllCompanySelect(@RequestParam(required = false) String queryParam) {
        List<CompanySelect> companySelects = companyService.findAllCompanySelect(queryParam);
        return ResponseEntity.ok(companySelects);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<CompanySelect>> searchCompanyByNameContaining(@RequestParam(required = false) String searchQuery) {
        List<CompanySelect> companies = companyService.searchCompanyByNameContaining(searchQuery);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long companyId) {
        CompanyDto company = companyService.findById(companyId);
        return ResponseEntity.ok(company);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto) {
        CompanyDto createdCompany = companyService.createCompany(companyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable("companyId") Long companyId, @RequestBody CompanyDto companyDto) {
        CompanyDto updatedCompany = companyService.updateCompany(companyId, companyDto);
        return ResponseEntity.ok(updatedCompany);
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
