package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.entities.Person;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PersonMapper {
    @Mapping(source = "recipientId", target = "recipient.id")
    Person toEntity(PersonDto personDto);

    @Mapping(source = "recipient.id", target = "recipientId")
    PersonDto toDto(Person person);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "recipientId", target = "recipient.id")
    Person partialUpdate(PersonDto personDto, @MappingTarget Person person);
}