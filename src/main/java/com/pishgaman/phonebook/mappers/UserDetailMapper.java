package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.UserDetailDto;
import com.pishgaman.phonebook.security.user.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserDetailMapper {
    User toEntity(UserDetailDto userDetailDto);

    UserDetailDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDetailDto userDetailDto, @MappingTarget User user);
}