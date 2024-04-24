package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Shareholder;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareholderRepository extends JpaRepository<Shareholder, Long>, JpaSpecificationExecutor<Shareholder> {
    @Query("select (count(s) > 0) from Shareholder s where s.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select (count(s) > 0) from Shareholder s where s.person.id = :personId")
    boolean existsByPersonId(@Param("personId") Long personId);

    @Query("select s from Shareholder s where s.company.id = :company_id")
    List<Shareholder> findAllByCompanyId(@Param("company_id") Long company_id);

    @Query("""
            select (count(s) > 0) from Shareholder s
            where s.createdBy = :createdBy or s.lastModifiedBy = :lastModifiedBy""")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);
}