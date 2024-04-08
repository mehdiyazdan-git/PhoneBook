package com.pishgaman.phonebook.mappers;

import com.pishgaman.phonebook.dtos.TaxPaymentSlipDetailDto;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.mapstruct.*;

public interface TaxPaymentSlipDetailMapper {
    @Mapping(source = "companyCompanyName", target = "company.companyName")
    @Mapping(source = "companyId", target = "company.id")
    TaxPaymentSlip toEntity(TaxPaymentSlipDetailDto taxPaymentSlipDetailDto);

    @InheritInverseConfiguration(name = "toEntity")
    TaxPaymentSlipDetailDto toDto(TaxPaymentSlip taxPaymentSlip);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TaxPaymentSlip partialUpdate(TaxPaymentSlipDetailDto taxPaymentSlipDetailDto, @MappingTarget TaxPaymentSlip taxPaymentSlip);
}
