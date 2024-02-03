package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.RecipientDto;
import com.pishgaman.phonebook.entities.Recipient;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RecipientMapper {
    Recipient toEntity(RecipientDto recipientDto);

    RecipientDto toDto(Recipient recipient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Recipient partialUpdate(RecipientDto recipientDto, @MappingTarget Recipient recipient);
}