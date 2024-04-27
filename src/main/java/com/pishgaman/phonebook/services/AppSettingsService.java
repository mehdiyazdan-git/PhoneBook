package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.AppSettingsDto;
import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.mappers.AppSettingsMapper;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(AppSettingsService.class);

    private final AppSettingsRepository appSettingsRepository;
    private final AppSettingsMapper appSettingsMapper;

    public AppSettingsDto find() {
        Optional<AppSettings> appSettings = appSettingsRepository.findById(1L);
        if (appSettings.isPresent()) {
            logger.info("AppSettings found with ID: {}", 1L);
            return convertToDto(appSettings.get());
        } else {
            logger.warn("AppSettings not found with ID: {}", 1L);
            return null;
        }
    }

    public AppSettingsDto update(AppSettingsDto appSettingsDto) {
        logger.info("Attempting to update AppSettings with ID: {}", 1L);
        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow(() -> {
            logger.error("AppSettings not found with ID: {}", 1L);
            return new EntityNotFoundException("AppSettings not found");
        });
        logger.info("AppSettings found, updating fields now...");
        appSettings.setDatabaseName(appSettingsDto.getDatabaseName());
        appSettings.setBackupPath(appSettingsDto.getBackupPath());
        appSettings.setVsphereUrl(appSettingsDto.getVsphereUrl());
        appSettings.setVsphereUsername(appSettingsDto.getVsphereUsername());
        appSettings.setVspherePassword(appSettingsDto.getVspherePassword());
        appSettings.setMaxUploadFileSize(appSettingsDto.getMaxUploadFileSize());
        AppSettings savedSettings = appSettingsRepository.save(appSettings);
        logger.info("AppSettings updated successfully with ID: {}", 1L);
        return appSettingsMapper.toDto(savedSettings);
    }

    private AppSettingsDto convertToDto(AppSettings appSettings) {
        AppSettingsDto appSettingsDto = new AppSettingsDto();
        BeanUtils.copyProperties(appSettings, appSettingsDto);
        logger.debug("Converted AppSettings to AppSettingsDto with ID: {}", appSettings.getId());
        return appSettingsDto;
    }
}
