package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.services.AppSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/settings")
public class AppSettingsController {

    private final AppSettingsService appSettingsService;

    @Autowired
    public AppSettingsController(AppSettingsService appSettingsService) {
        this.appSettingsService = appSettingsService;
    }

    @GetMapping("/max-upload-file-size")
    public ResponseEntity<Long> getMaxUploadFileSize() {
        return appSettingsService.getAppSettings()
                .map(settings -> ResponseEntity.ok(settings.getMaxUploadFileSize()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/max-upload-file-size/{maxUploadFileSize}")
    public ResponseEntity<Void> updateMaxUploadFileSize(@PathVariable long maxUploadFileSize) {
        appSettingsService.updateMaxUploadFileSize(maxUploadFileSize);
        return ResponseEntity.ok().build();
    }
}
