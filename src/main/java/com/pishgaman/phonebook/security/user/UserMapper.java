package com.pishgaman.phonebook.security.user;

import com.pishgaman.phonebook.security.token.TokenMapper;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,  componentModel = "spring", uses = {TokenMapper.class})
public interface UserMapper {
    User toEntity(UserDto userDto);

    @AfterMapping
    default void linkTokens(@MappingTarget User user) {
        user.getTokens().forEach(token -> token.setUser(user));
    }

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);
}