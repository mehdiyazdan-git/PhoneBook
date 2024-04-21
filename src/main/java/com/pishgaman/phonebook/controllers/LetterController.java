package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import com.pishgaman.phonebook.services.LetterService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/letters")
public class LetterController {

    private final LetterService letterService;

    @Autowired
    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }


    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLettersToExcel() throws IOException {
        byte[] excelData = letterService.exportLettersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_letters.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }


    @GetMapping(path = {"/",""})
    public ResponseEntity<List<LetterDto>> getAllLetters() {
        List<LetterDto> letters = letterService.getAllLetters();
        return ResponseEntity.ok(letters);
    }
    @GetMapping(path = "/pageable")
    public ResponseEntity<Page<LetterDetailsDto>> getAllLetterDetails(
            @RequestParam Optional<Long> companyId,
            @RequestParam Optional<Long> yearId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            LetterSearch search) {

        companyId.ifPresent(search::setCompanyId); // Set senderId in search criteria if present
        yearId.ifPresent(search::setYearId); // Set receiverId in search criteria if present

        Page<LetterDetailsDto> letters = letterService.findAllLetterDetails(search, page, size, sortBy, order);
        return ResponseEntity.ok(letters);
    }
    @GetMapping("/download-all-letters.xlsx")
    public ResponseEntity<byte[]> downloadAllLettersExcel() throws IOException {
        byte[] excelData = letterService.exportLettersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_letters.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importLettersFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = letterService.importLettersFromExcel(file);
            return ResponseEntity.ok(message);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadLetterTemplate() throws IOException {
        byte[] templateBytes = letterService.generateLetterTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "letter_template.xlsx");
        headers.setContentType(FileMediaType.getMediaType("xlsx"));

        return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/{letterId}")
    public ResponseEntity<LetterDto> getLetterById(@PathVariable Long letterId) {
        LetterDto letter = letterService.getLetterById(letterId);
        return ResponseEntity.ok(letter);
    }

    @PostMapping(path = {"/",""})
    public ResponseEntity<String> createLetter(@RequestBody LetterDto letterDto) {
        try {
            letterService.createLetter(letterDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("نامه با موفقیت ایجاد شد");
        }catch (IllegalArgumentException | EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{letterId}")
    public ResponseEntity<String> updateLetter(@PathVariable Long letterId, @RequestBody LetterDto letterDto) {
        try {
            letterService.updateLetter(letterId, letterDto);
            return ResponseEntity.ok("نامه با موفقیت ویرایش شد.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{letterId}/update-state/{letterState}")
    public ResponseEntity<String> updateLetterState(
            @PathVariable Long letterId,
            @PathVariable LetterState letterState) {

        try {
            letterService.updateLetterState(letterId, letterState);
            return ResponseEntity.ok("وضعیت نامه با موفقیت ویرایش شد.");
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطا در ویرایش وضعیت نامه");
        }
    }

    @DeleteMapping(path = "/{letterId}")
    public ResponseEntity<String> deleteLetter(@PathVariable("letterId") Long letterId) {
        try {
            String message = letterService.deleteLetter(letterId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }catch (IllegalArgumentException | EntityNotFoundException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
