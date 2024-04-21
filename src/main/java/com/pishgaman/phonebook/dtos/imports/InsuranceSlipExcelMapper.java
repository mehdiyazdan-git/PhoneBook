package com.pishgaman.phonebook.dtos.imports;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InsuranceSlipExcelMapper {
    @Mapping(source = "companyId", target = "company.id")
    InsuranceSlip toEntity(InsuranceSlipExcelDto insuranceSlipExcelDto);

    @Mapping(source = "company.id", target = "companyId")
    InsuranceSlipExcelDto toDto(InsuranceSlip insuranceSlip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "companyId", target = "company.id")
    InsuranceSlip partialUpdate(InsuranceSlipExcelDto insuranceSlipExcelDto, @MappingTarget InsuranceSlip insuranceSlip);
}