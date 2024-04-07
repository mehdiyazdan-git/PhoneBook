package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.YearDto;
import com.pishgaman.phonebook.searchforms.YearSearch;
import com.pishgaman.phonebook.services.YearService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/years")
@RequiredArgsConstructor
public class YearController {

    private final YearService yearService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<YearDto>> getAllYears(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,YearSearch search) {
        Page<YearDto> years = yearService.findAll(page, size, sortBy, order,search);
        return ResponseEntity.ok(years);
    }

    @GetMapping(path = "/select")
    public ResponseEntity<List<YearDto>> findAllYearSelect(
            @RequestParam(required = false) String searchQuery) {
        YearSearch yearSearch = new YearSearch();
        if (searchQuery != null) {
            try {
                Long name = Long.parseLong(searchQuery);
                yearSearch.setName(name);
            } catch (NumberFormatException e) {
                // Handle the case where searchQuery is not a valid Long
            }
        }
        List<YearDto> years = yearService.findAllYearSelect(yearSearch);
        return ResponseEntity.ok(years);
    }



    @GetMapping("/{id}")
    public ResponseEntity<YearDto> getYearById(@PathVariable Long id) {
        YearDto year = yearService.findById(id);
        return ResponseEntity.ok(year);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<YearDto> createYear(@RequestBody YearDto yearDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(yearService.createYear(yearDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<YearDto> updateYear(@PathVariable Long id, @RequestBody YearDto yearDto) {
        try {
            YearDto updatedYear = yearService.updateYear(id, yearDto);
            return ResponseEntity.ok(updatedYear);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYear(@PathVariable Long id) {
        try {
            yearService.deleteYear(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }
}
