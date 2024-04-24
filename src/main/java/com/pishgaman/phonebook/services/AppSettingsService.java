package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.AppSettingsDto;
import com.pishgaman.phonebook.entities.AppSettings;
import com.pishgaman.phonebook.repositories.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppSettingsService {
    private final AppSettingsRepository appSettingsRepository;

    public AppSettingsDto find() {
        Optional<AppSettings> appSettings = appSettingsRepository.findById(1L);
        return appSettings.map(this::convertToDto).orElse(null);
    }

    public AppSettingsDto update(AppSettingsDto appSettingsDto) {
        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow();
        BeanUtils.copyProperties(appSettingsDto, appSettings, "id");
        AppSettings updatedAppSettings = appSettingsRepository.save(appSettings);
        return convertToDto(updatedAppSettings);
    }

    private AppSettingsDto convertToDto(AppSettings appSettings) {
        AppSettingsDto appSettingsDto = new AppSettingsDto();
        BeanUtils.copyProperties(appSettings, appSettingsDto);
        return appSettingsDto;
    }
}

