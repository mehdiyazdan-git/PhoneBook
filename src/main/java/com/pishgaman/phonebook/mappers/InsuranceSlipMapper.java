package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.InsuranceSlipDto;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InsuranceSlipMapper {
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "companyId", target = "company.id")
    InsuranceSlip toEntity(InsuranceSlipDto insuranceSlipDto);

    @InheritInverseConfiguration(name = "toEntity")
    InsuranceSlipDto toDto(InsuranceSlip insuranceSlip);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    InsuranceSlip partialUpdate(InsuranceSlipDto insuranceSlipDto, @MappingTarget InsuranceSlip insuranceSlip);
}