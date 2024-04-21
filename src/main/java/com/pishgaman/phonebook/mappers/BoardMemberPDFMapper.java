package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.BoardMemberPDFDto;
import com.pishgaman.phonebook.entities.BoardMember;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BoardMemberPDFMapper {
    @Mapping(source = "positionName", target = "position.name")
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "personLastName", target = "person.lastName")
    @Mapping(source = "personFirstName", target = "person.firstName")
    BoardMember toEntity(BoardMemberPDFDto boardMemberPDFDto);

    @InheritInverseConfiguration(name = "toEntity")
    BoardMemberPDFDto toDto(BoardMember boardMember);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoardMember partialUpdate(BoardMemberPDFDto boardMemberPDFDto, @MappingTarget BoardMember boardMember);
}