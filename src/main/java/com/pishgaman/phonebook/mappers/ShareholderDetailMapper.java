package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.ShareholderDetailDto;
import com.pishgaman.phonebook.entities.Shareholder;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ShareholderDetailMapper {
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "personLastName", target = "person.lastName")
    @Mapping(source = "personFirstName", target = "person.firstName")
    @Mapping(source = "personId", target = "person.id")
    Shareholder toEntity(ShareholderDetailDto shareholderDetailDto);

    @InheritInverseConfiguration(name = "toEntity")
    ShareholderDetailDto toDto(Shareholder shareholder);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Shareholder partialUpdate(ShareholderDetailDto shareholderDetailDto, @MappingTarget Shareholder shareholder);

    @AfterMapping
    default void setHasFileFlag(@MappingTarget ShareholderDetailDto shareholderDetailDto, Shareholder shareholder) {
        shareholderDetailDto.setHasFile(shareholder.getScannedShareCertificate() != null);
    }
}