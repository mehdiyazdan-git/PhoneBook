package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.entities.BoardMember;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BoardMemberDetailsMapper {

    // Direct mappings from DTO to Entity
    @Mapping(target = "position.name", source = "positionName")
    @Mapping(target = "position.id", source = "positionId")
    @Mapping(target = "company.companyName", source = "companyCompanyName")
    @Mapping(target = "company.id", source = "companyId")
    @Mapping(target = "person.lastName", source = "personLastName")
    @Mapping(target = "person.firstName", source = "personFirstName")
    @Mapping(target = "person.id", source = "personId")
    BoardMember toEntity(BoardMemberDetailsDto boardMemberDetailsDto);

    // Inverse mappings from Entity to DTO
    @InheritInverseConfiguration(name = "toEntity")
    @Mapping(target = "fullName", expression = "java(getFullName(boardMember))")
    BoardMemberDetailsDto toDto(BoardMember boardMember);

    // Partial update method
    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(BoardMemberDetailsDto boardMemberDetailsDto, @MappingTarget BoardMember boardMember);

    // Default method to generate the full name from the associated Person entity
    default String getFullName(BoardMember boardMember) {
        if (boardMember.getPerson() != null) {
            return boardMember.getPerson().getFirstName() + " " + boardMember.getPerson().getLastName();
        }
        return "";
    }
}
