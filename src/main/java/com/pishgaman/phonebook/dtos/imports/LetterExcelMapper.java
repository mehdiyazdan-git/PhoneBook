package com.pishgaman.phonebook.dtos.imports;

import com.pishgaman.phonebook.entities.Letter;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LetterExcelMapper {
    @Mapping(source = "yearId", target = "year.id")
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "customerCreatedBy", target = "customer.createdBy")
    @Mapping(source = "letterTypeId", target = "letterType.id")
    Letter toEntity(LetterExcelDto letterExcelDto);

    @InheritInverseConfiguration(name = "toEntity")
    LetterExcelDto toDto(Letter letter);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Letter partialUpdate(LetterExcelDto letterExcelDto, @MappingTarget Letter letter);
}