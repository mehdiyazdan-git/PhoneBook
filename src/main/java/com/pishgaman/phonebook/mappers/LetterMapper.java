package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.entities.Letter;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LetterMapper {
    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "recipientId", target = "recipient.id")
    @Mapping(source = "yearId", target = "year.id")
    Letter toEntity(LetterDto letterDto);

    @InheritInverseConfiguration(name = "toEntity")
    LetterDto toDto(Letter letter);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Letter partialUpdate(LetterDto letterDto, @MappingTarget Letter letter);

}