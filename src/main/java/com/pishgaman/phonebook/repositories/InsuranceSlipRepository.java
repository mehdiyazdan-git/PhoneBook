package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.InsuranceSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceSlipRepository extends JpaRepository<InsuranceSlip, Long>, JpaSpecificationExecutor<InsuranceSlip> {
    @Query("select (count(i) > 0) from InsuranceSlip i where i.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
}