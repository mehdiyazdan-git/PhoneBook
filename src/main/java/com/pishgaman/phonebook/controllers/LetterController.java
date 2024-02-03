package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.services.LetterService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/letters")
public class LetterController {

    private final LetterService letterService;

    @Autowired
    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }

    @GetMapping(path = {"/",""})
    public ResponseEntity<List<LetterDto>> getAllLetters() {
        List<LetterDto> letters = letterService.getAllLetters();
        return ResponseEntity.ok(letters);
    }
    @GetMapping(path = "/all-by-sender-id/{senderId}")
    public ResponseEntity<List<LetterDetailsDto>> findLetterDetailsBySenderId(@PathVariable("senderId") Long senderId) {
        List<LetterDetailsDto> letters = letterService.findLetterDetailsBySenderId(senderId);
        return ResponseEntity.ok(letters);
    }
    @GetMapping("/details")
    public ResponseEntity<List<LetterDetailsDto>> getLetterDetails() {
        List<LetterDetailsDto> letterDetails = letterService.getLetterDetails();
        return ResponseEntity.ok(letterDetails);
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
            return ResponseEntity.status(HttpStatus.CREATED).body("letter created successfully");
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{letterId}")
    public ResponseEntity<String> updateLetter(@PathVariable Long letterId, @RequestBody LetterDto letterDto) {
        try {
            letterService.updateLetter(letterId, letterDto);
            return ResponseEntity.status(HttpStatus.OK).body("letter updated successfully");
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PutMapping("/{letterId}/update-state/{letterState}")
    public ResponseEntity<String> updateLetterState(
            @PathVariable Long letterId,
            @PathVariable LetterState letterState) {

        try {
            letterService.updateLetterState(letterId, letterState);
            return ResponseEntity.ok("Letter state updated successfully.");
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating letter state.");
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
