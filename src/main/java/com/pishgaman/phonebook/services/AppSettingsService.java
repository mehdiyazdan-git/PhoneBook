package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppSettingsService {

    private final AppSettingsRepository appSettingsRepository;

    @Autowired
    public AppSettingsService(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    public Optional<AppSettings> getAppSettings() {
        return appSettingsRepository.findById(1L);
    }

    @Transactional
    public void updateMaxUploadFileSize(long maxUploadFileSize) {
        AppSettings appSettings = getAppSettings().orElseThrow(() -> new RuntimeException("App settings not found"));
        appSettings.setMaxUploadFileSize(maxUploadFileSize);
        appSettingsRepository.save(appSettings);
    }
}

