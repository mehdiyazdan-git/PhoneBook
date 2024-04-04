package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.searchforms.PositionSearch;
import com.pishgaman.phonebook.services.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
