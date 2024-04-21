package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service@RequiredArgsConstructor
public class AppSettingsService {
    private final AppSettingsRepository appSettingsRepository;

    @Transactional(readOnly = true)
    public Optional<AppSettings> getAppSettings() {
        return appSettingsRepository.findById(1L);
    }

    @Transactional
    public void updateMaxUploadFileSize(long maxUploadFileSize) {
        AppSettings appSettings = getAppSettings().orElseThrow(() -> new RuntimeException("App settings not found"));
        appSettings.setMaxUploadFileSize(maxUploadFileSize);
        appSettingsRepository.save(appSettings);
    }

    @Transactional
    public AppSettings updateVSphereSettings(String url, String username, String password) {
        AppSettings appSettings = getAppSettings().orElseThrow(() -> new RuntimeException("App settings not found"));
        appSettings.setVsphereUrl(url);
        appSettings.setVsphereUsername(username);
        appSettings.setVspherePassword(password);
        return appSettingsRepository.save(appSettings);
    }
}

