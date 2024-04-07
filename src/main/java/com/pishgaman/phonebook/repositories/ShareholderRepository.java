package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Shareholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareholderRepository extends JpaRepository<Shareholder, Long>, JpaSpecificationExecutor<Shareholder> {
    @Query("select (count(s) > 0) from Shareholder s where s.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select (count(s) > 0) from Shareholder s where s.person.id = :personId")
    boolean existsByPersonId(@Param("personId") Long personId);
}