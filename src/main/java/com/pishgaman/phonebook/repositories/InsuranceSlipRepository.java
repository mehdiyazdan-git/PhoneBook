package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceSlipRepository extends JpaRepository<InsuranceSlip, Long>, JpaSpecificationExecutor<InsuranceSlip> {
    @Query("select (count(i) > 0) from InsuranceSlip i where i.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select i from InsuranceSlip i where i.company.id = :company_id")
    List<InsuranceSlip> findAllByCompanyId(@Param("company_id") Long company_id);

    @Query("""
            select (count(i) > 0) from InsuranceSlip i
            where i.createdBy = :createdBy or i.lastModifiedBy = :lastModifiedBy""")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);
}