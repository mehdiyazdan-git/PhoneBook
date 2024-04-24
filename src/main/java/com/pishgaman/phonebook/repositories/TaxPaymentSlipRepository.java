package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxPaymentSlipRepository extends JpaRepository<TaxPaymentSlip, Long>, JpaSpecificationExecutor<TaxPaymentSlip> {

    @Query("select (count(t) > 0) from TaxPaymentSlip t where t.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select t from TaxPaymentSlip t where t.company.id = :company_id")
    List<TaxPaymentSlip> findAllByCompanyId(@Param("company_id") Long company_id);

    @Query("""
            select (count(t) > 0) from TaxPaymentSlip t
            where t.createdBy = :createdBy or t.lastModifiedBy = :lastModifiedBy""")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);
}