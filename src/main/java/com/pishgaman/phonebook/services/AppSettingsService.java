package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.AppSettingsDto;
import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.mappers.AppSettingsMapper;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppSettingsService {

    private final AppSettingsRepository appSettingsRepository;
    private final AppSettingsMapper appSettingsMapper;

    public AppSettingsDto find() {
        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("AppSettings not found"));
        return appSettingsMapper.toDto(appSettings);
    }

    public AppSettingsDto update(AppSettingsDto appSettingsDto) {

        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("AppSettings not found"));

        appSettings.setDatabaseName(appSettingsDto.getDatabaseName());
        appSettings.setBackupPath(appSettingsDto.getBackupPath());
        appSettings.setVsphereUrl(appSettingsDto.getVsphereUrl());
        appSettings.setVsphereUsername(appSettingsDto.getVsphereUsername());
        appSettings.setVspherePassword(appSettingsDto.getVspherePassword());
        appSettings.setMaxUploadFileSize(appSettingsDto.getMaxUploadFileSize());
        AppSettings savedSettings = appSettingsRepository.save(appSettings);

        return appSettingsMapper.toDto(savedSettings);
    }
}
