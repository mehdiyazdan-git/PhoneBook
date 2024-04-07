package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.ShareholderDto;
import com.pishgaman.phonebook.entities.Shareholder;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ShareholderMapper {
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "personId", target = "person.id")
    Shareholder toEntity(ShareholderDto shareholderDto);

    @InheritInverseConfiguration(name = "toEntity")
    ShareholderDto toDto(Shareholder shareholder);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Shareholder partialUpdate(ShareholderDto shareholderDto, @MappingTarget Shareholder shareholder);
}