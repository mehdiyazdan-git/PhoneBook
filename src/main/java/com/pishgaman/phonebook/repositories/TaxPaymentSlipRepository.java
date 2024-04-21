package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxPaymentSlipRepository extends JpaRepository<TaxPaymentSlip, Long>, JpaSpecificationExecutor<TaxPaymentSlip> {

    @Query("select (count(t) > 0) from TaxPaymentSlip t where t.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
}