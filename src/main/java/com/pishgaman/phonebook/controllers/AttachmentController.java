package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.AttachListDto;
import com.pishgaman.phonebook.dtos.AttachmentDto;
import com.pishgaman.phonebook.entities.Attachment;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.mappers.AttachmentMapper;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.services.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;
    private final LetterRepository letterRepository;

    @Autowired
    public AttachmentController(
            AttachmentService attachmentService,
            AttachmentMapper attachmentMapper,
            LetterRepository letterRepository
    ) {
        this.attachmentService = attachmentService;
        this.attachmentMapper = attachmentMapper;
        this.letterRepository = letterRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadToDB(@RequestParam("letterId") Long letterId,
                                        @org.jetbrains.annotations.NotNull @RequestParam("file") MultipartFile[] files) {
        // Iterate through each file
        for (MultipartFile file : files) {
            Attachment attachment = new Attachment();
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            attachment.setFileName(fileName);
            try {
                attachment.setFileContent(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Error uploading file: " + fileName);
            }
            attachment.setFileType(file.getContentType());
            Letter letter = letterRepository.findById(letterId)
                    .orElseThrow(() -> new EntityNotFoundException("نامه با شناسه " + letterId + " یافت نشد."));
            attachment.setLetter(letter);
            attachmentService.createAttachment(attachmentMapper.toDto(attachment));
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(Objects.requireNonNull(files[0].getOriginalFilename())).path("/db")
                .toUriString();
        return ResponseEntity.ok(fileDownloadUri);
    }

    @GetMapping(path = "/download/{attachId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("attachId") Long attachId) {
        Attachment attachment = attachmentService.findById(attachId);
        ByteArrayResource resource = new ByteArrayResource(attachment.getFileContent());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment.getFileName())
                .body(resource);
    }


    @GetMapping(path = {"/all-by-letter-id/{letterId}"})
    public ResponseEntity<List<AttachListDto>> getAllAttachments(@PathVariable("letterId") Long letterId) {
        List<AttachListDto> allByLetterId = attachmentService.findAllByLetterId(letterId);
        return ResponseEntity.ok(allByLetterId);
    }

    @GetMapping("/{attachId}")
    public ResponseEntity<AttachmentDto> getAttachmentById(@PathVariable("attachId") Long attachId) {
        Attachment attachment = attachmentService.findById(attachId);
        return ResponseEntity.ok(attachmentMapper.toDto(attachment));
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<AttachmentDto> createAttachment(@RequestBody AttachmentDto attachmentDto) {
        AttachmentDto createdAttachment = attachmentService.createAttachment(attachmentDto);
        return new ResponseEntity<>(createdAttachment, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{attachId}")
    public ResponseEntity<AttachmentDto> updateAttachment(@PathVariable("attachId") Long attachId, @RequestBody AttachmentDto attachmentDto) {
        AttachmentDto updatedAttachment = attachmentService.updateAttachment(attachId, attachmentDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAttachment);
    }

    @DeleteMapping("/{attachId}")
    public ResponseEntity<String> deleteAttachment(@PathVariable("attachId") Long attachId) {
        try {
            attachmentService.removeAttachment(attachId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
    }
}
