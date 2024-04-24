package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.AppSettingsDto;
import com.pishgaman.phonebook.entities.AppSettings;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AppSettingsMapper {
    AppSettings toEntity(AppSettingsDto appSettingsDto);

    AppSettingsDto toDto(AppSettings appSettings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AppSettings partialUpdate(AppSettingsDto appSettingsDto, @MappingTarget AppSettings appSettings);
}