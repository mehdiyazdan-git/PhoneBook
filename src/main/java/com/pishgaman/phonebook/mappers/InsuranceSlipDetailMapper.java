package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.InsuranceSlipDetailDto;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InsuranceSlipDetailMapper {
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "companyId", target = "company.id")
    InsuranceSlip toEntity(InsuranceSlipDetailDto insuranceSlipDetailDto);

    @InheritInverseConfiguration(name = "toEntity")
    InsuranceSlipDetailDto toDto(InsuranceSlip insuranceSlip);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    InsuranceSlip partialUpdate(InsuranceSlipDetailDto insuranceSlipDetailDto, @MappingTarget InsuranceSlip insuranceSlip);
}