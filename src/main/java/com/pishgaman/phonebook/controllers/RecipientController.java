package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.RecipientDto;
import com.pishgaman.phonebook.dtos.SenderDto;
import com.pishgaman.phonebook.services.RecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;

    @Autowired
    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @GetMapping(path = {"/",""})
    public ResponseEntity<List<RecipientDto>> getAllRecipients() {
        List<RecipientDto> recipients = recipientService.findAll();
        return ResponseEntity.ok(recipients);
    }
    @GetMapping(path = "/search")
    public ResponseEntity<List<RecipientDto>> searchSenderByNameContaining(@RequestParam("searchQuery") String searchQuery) {
        List<RecipientDto> senders = recipientService.searchRecipientByNameContaining(searchQuery);
        return ResponseEntity.ok(senders);
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientDto> getRecipientById(@PathVariable Long recipientId) {
        RecipientDto recipient = recipientService.findById(recipientId);
        return ResponseEntity.ok(recipient);
    }

    @PostMapping(path = {"/",""})
    public ResponseEntity<RecipientDto> createRecipient(@RequestBody RecipientDto recipientDto) {
        RecipientDto createdRecipient = recipientService.createRecipient(recipientDto);
        return new ResponseEntity<>(createdRecipient, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{recipientId}")
    public ResponseEntity<RecipientDto> updateRecipient(@PathVariable("recipientId") Long recipientId, @RequestBody RecipientDto recipientDto) {
        RecipientDto updatedRecipient = recipientService.updateRecipient(recipientId, recipientDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRecipient);
    }

    @DeleteMapping("/{recipientId}")
    public ResponseEntity<String> deleteRecipient(@PathVariable("recipientId") Long recipientId) {
        try {
            String message = recipientService.removeRecipient(recipientId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
