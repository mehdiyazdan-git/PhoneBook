package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/positions")
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping(path = {"/", ""})
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        List<PositionDto> positions = positionService.findAll();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long positionId) {
        PositionDto position = positionService.findPositionById(positionId);
        return ResponseEntity.ok(position);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<PositionDto> createPosition(@RequestBody PositionDto positionDto) {
        PositionDto createdPosition = positionService.createPosition(positionDto);
        return new ResponseEntity<>(createdPosition, HttpStatus.CREATED);
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<PositionDto> updatePosition(@PathVariable("positionId") Long positionId, @RequestBody PositionDto positionDto) {
        PositionDto updatedPosition = positionService.updatePosition(positionId, positionDto);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<String> deletePosition(@PathVariable("positionId") Long positionId) {
        String message = positionService.removePosition(positionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
    }

    // Additional endpoints related to Position can be added here
}
