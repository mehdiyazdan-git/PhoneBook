package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.BaseDto;
import com.pishgaman.phonebook.entities.BaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BaseEntityMapper {
    BaseDto toDto(BaseEntity baseEntity);
}