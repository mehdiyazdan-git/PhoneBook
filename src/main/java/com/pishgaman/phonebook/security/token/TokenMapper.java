package com.pishgaman.phonebook.security.token;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TokenMapper {
    Token toEntity(TokenDto tokenDto);

    TokenDto toDto(Token token);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Token partialUpdate(TokenDto tokenDto, @MappingTarget Token token);
}