package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSettingsInitializer {
    private final AppSettingsRepository appSettingsRepository;

    @PostConstruct
    public void init() {
        // Check if the table is empty
        if (appSettingsRepository.count() == 0) {
            AppSettings appSettings = new AppSettings();
            appSettings.setMaxUploadFileSize(1048576); // Set default max upload file size to 1 MB
            appSettingsRepository.save(appSettings);
        }
    }
}

