package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    @Query("select c from Company c where c.companyName = ?1")
    Company findCompanyByCompanyName(@Param("companyName") String companyName);

    @Query("select c from Company c where c.companyName = :companyName and c.id <> :id")
    Company findCompanyByCompanyNameAndIdNot(@Param("companyName") String companyName, @Param("id") Long id);

    @Query("select c from Company c where c.companyName like concat('%', :companyName, '%')")
    List<Company> findByCompanyNameContains(@Param("companyName") String companyName);

    @Query(value = "SELECT MAX(letter_counter) AS max_letter_counter FROM public.company WHERE id = :companyId",nativeQuery = true)
    Integer getMaxLetterCountByCompanyId(Long companyId);

    @Modifying
    @Query(value = "UPDATE public.company SET letter_counter = :count WHERE id = :companyId",nativeQuery = true)
    void incrementLetterCountByOne(Integer count,Long companyId);
}