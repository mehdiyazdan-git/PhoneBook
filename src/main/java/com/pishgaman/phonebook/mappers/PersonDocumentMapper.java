package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.PersonDocumentDto;
import com.pishgaman.phonebook.entities.PersonDocument;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PersonDocumentMapper {
    @Mapping(source = "personId", target = "person.id")
    PersonDocument toEntity(PersonDocumentDto personDocumentDto);

    @Mapping(source = "person.id", target = "personId")
    PersonDocumentDto toDto(PersonDocument personDocument);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "personId", target = "person.id")
    PersonDocument partialUpdate(PersonDocumentDto personDocumentDto, @MappingTarget PersonDocument personDocument);
}