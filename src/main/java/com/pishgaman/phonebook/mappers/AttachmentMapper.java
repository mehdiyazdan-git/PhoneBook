package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.AttachmentDto;
import com.pishgaman.phonebook.entities.Attachment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(source = "letterId", target = "letter.id")
    Attachment toEntity(AttachmentDto attachmentDto);

    @Mapping(source = "letter.id", target = "letterId")
    AttachmentDto toDto(Attachment attachment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "letterId", target = "letter.id")
    Attachment partialUpdate(AttachmentDto attachmentDto, @MappingTarget Attachment attachment);
}