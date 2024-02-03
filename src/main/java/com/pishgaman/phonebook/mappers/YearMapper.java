package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.YearDto;
import com.pishgaman.phonebook.entities.Year;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface YearMapper {
    Year toEntity(YearDto yearDto);

    YearDto toDto(Year year);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Year partialUpdate(YearDto yearDto, @MappingTarget Year year);
}