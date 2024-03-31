package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.entities.Document;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DocumentMapper {
    @Mapping(source = "personId", target = "person.id")
    Document toEntity(DocumentDto documentDto);

    @Mapping(source = "person.id", target = "personId")
    DocumentDto toDto(Document document);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "personId", target = "person.id")
    Document partialUpdate(DocumentDto documentDto, @MappingTarget Document document);

    DocumentDetailDto toDocumentDetailDto(Document document);
}