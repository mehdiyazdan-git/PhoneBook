package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.SenderDto;
import com.pishgaman.phonebook.services.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/senders")
public class SenderController {

    private final SenderService senderService;

    @Autowired
    public SenderController(SenderService senderService) {
        this.senderService = senderService;
    }

    @GetMapping(path = {"/",""})
    public ResponseEntity<List<SenderDto>> getAllSenders() {
        List<SenderDto> senders = senderService.findAll();
        return ResponseEntity.ok(senders);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<SenderDto>> searchSenderByNameContaining(@RequestParam("searchQuery") String searchQuery) {
        List<SenderDto> senders = senderService.searchSenderByNameContaining(searchQuery);
        return ResponseEntity.ok(senders);
    }

    @GetMapping("/{senderId}")
    public ResponseEntity<SenderDto> getSenderById(@PathVariable Long senderId) {
        SenderDto sender = senderService.findById(senderId);
        return ResponseEntity.ok(sender);
    }

    @PostMapping(path = {"/",""})
    public ResponseEntity<SenderDto> createSender(@RequestBody SenderDto senderDto) {
        SenderDto createdSender = senderService.createSender(senderDto);
        return new ResponseEntity<>(createdSender, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{senderId}")
    public ResponseEntity<SenderDto> updateSender(@PathVariable("senderId") Long senderId, @RequestBody SenderDto senderDto) {
        SenderDto updatedSender = senderService.updateSender(senderId, senderDto);
        return ResponseEntity.ok(updatedSender);
    }

    @DeleteMapping(path = "/{senderId}")
    public ResponseEntity<String> deleteSender(@PathVariable("senderId") Long senderId) {
        try {
            String message = senderService.removeSender(senderId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
