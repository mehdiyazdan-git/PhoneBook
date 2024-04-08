package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TaxPaymentSlipMapper {
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "companyId", target = "company.id")
    TaxPaymentSlip toEntity(TaxPaymentSlipDto taxPaymentSlipDto);

    @InheritInverseConfiguration(name = "toEntity")
    TaxPaymentSlipDto toDto(TaxPaymentSlip taxPaymentSlip);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TaxPaymentSlip partialUpdate(TaxPaymentSlipDto taxPaymentSlipDto, @MappingTarget TaxPaymentSlip taxPaymentSlip);
}