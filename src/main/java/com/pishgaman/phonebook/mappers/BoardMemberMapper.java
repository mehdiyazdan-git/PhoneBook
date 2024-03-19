package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.entities.BoardMember;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BoardMemberMapper {
    @Mapping(source = "positionId", target = "position.id")
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(source = "personId", target = "person.id")
    BoardMember toEntity(BoardMemberDto boardMemberDto);

    @InheritInverseConfiguration(name = "toEntity")
    BoardMemberDto toDto(BoardMember boardMember);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoardMember partialUpdate(BoardMemberDto boardMemberDto, @MappingTarget BoardMember boardMember);
}