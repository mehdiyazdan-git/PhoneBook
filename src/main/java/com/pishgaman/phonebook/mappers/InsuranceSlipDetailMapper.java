package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.InsuranceSlipDetailDto;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InsuranceSlipDetailMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyCompanyName", source = "company.companyName")
    InsuranceSlipDetailDto toDto(InsuranceSlip insuranceSlip);

}
