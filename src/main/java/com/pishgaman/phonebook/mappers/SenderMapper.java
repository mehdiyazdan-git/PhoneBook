package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.SenderSelectDto;
import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.dtos.SenderDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SenderMapper {
    Sender toEntity(SenderDto senderDto);

    SenderDto toDto(Sender sender);
    SenderSelectDto toSelectDto(Sender sender);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Sender partialUpdate(SenderDto senderDto, @MappingTarget Sender sender);
}