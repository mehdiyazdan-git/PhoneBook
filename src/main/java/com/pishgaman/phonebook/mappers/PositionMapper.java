package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.entities.Position;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PositionMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    Position toEntity(PositionDto positionDto);
    @InheritConfiguration(name = "toEntity")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "id", target = "id")
    PositionDto toDto(Position position);
    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Position partialUpdate(PositionDto positionDto, @MappingTarget Position position);


}