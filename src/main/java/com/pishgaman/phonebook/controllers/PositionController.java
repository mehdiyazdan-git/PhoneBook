package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.CompanySelect;
import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.searchforms.PositionSearch;
import com.pishgaman.phonebook.services.PositionService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<PositionDto>> getAllPositions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order, PositionSearch search) {
        Page<PositionDto> positions = positionService.findAll(page, size, sortBy, order,search);
        return ResponseEntity.ok(positions);
    }

    @GetMapping(path = "/select")
    public ResponseEntity<List<PositionDto>> findAllPositionSelect(@RequestParam(required = false) String queryParam) {
        List<PositionDto> positionDtoList = positionService.findAllPositionSelect(queryParam);
        return ResponseEntity.ok(positionDtoList);
    }
    @GetMapping(path = "/search")
    public ResponseEntity<List<PositionDto>> searchCompanyByNameContaining(@RequestParam(required = false) String searchQuery) {
        List<PositionDto> dtoList = positionService.searchPositionByNameContaining(searchQuery);
        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/download-all-positions.xlsx")
    public ResponseEntity<byte[]> downloadAllPositionsExcel() throws IOException {
        byte[] excelData = positionService.exportPositionsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_positions.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import-positions")
    public ResponseEntity<String> importPositionsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = positionService.importPositionsFromExcel(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import positions from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadPositionTemplate() {
        try {
            byte[] templateBytes = positionService.generatePositionTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "position_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        PositionDto position = positionService.findById(id);
        return ResponseEntity.ok(position);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<PositionDto> createPosition(@RequestBody PositionDto positionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.createPosition(positionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionDto> updatePosition(@PathVariable Long id, @RequestBody PositionDto positionDto) {
        try {
            PositionDto updatedPosition = positionService.updatePosition(id, positionDto);
            return ResponseEntity.ok(updatedPosition);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }
}
