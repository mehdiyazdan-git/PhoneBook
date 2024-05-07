package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.AppSettingsDto;
import com.pishgaman.phonebook.services.AppSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/app-settings")
@RequiredArgsConstructor
public class AppSettingsController {
    private final AppSettingsService appSettingsService;


    @GetMapping("/max-upload-file-size")
    public ResponseEntity<Long> getMaxUploadFileSize() {
      return ResponseEntity.ok(appSettingsService.find().getMaxUploadFileSize());
    }

    @GetMapping
    public ResponseEntity<AppSettingsDto> getAppSettings() {
        AppSettingsDto appSettingsDto = appSettingsService.find();
        return ResponseEntity.ok(appSettingsDto);
    }

    @PutMapping
    public ResponseEntity<AppSettingsDto> updateAppSettings(@RequestBody AppSettingsDto appSettingsDto) {
        AppSettingsDto updatedAppSettingsDto = appSettingsService.update(appSettingsDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAppSettingsDto);
    }
}

