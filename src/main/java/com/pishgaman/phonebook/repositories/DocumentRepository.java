package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("""
            select d.id, d.documentName,d.documentType,d.fileExtension from Document d
            where d.person.id = :personId""")
    List<Object[]> findAllDocumentsByPersonId(@Param("personId") Long personId);

    @Query("""
            select d.id, d.documentName,d.documentType,d.fileExtension from Document d
            where d.company.id = :companyId""")
    List<Object[]> findAllDocumentsByCompanyId(@Param("companyId") Long companyId);


    @Query("""
            select d.id, d.documentName,d.documentType,d.fileExtension from Document d
            where d.letter.id = :letterId""")
    List<Object[]> findAllDocumentsByLetterId(@Param("letterId") Long letterId);

    @Query("select (count(d) > 0) from Document d where d.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select (count(d) > 0) from Document d where d.person.id = :personId")
    boolean existsByPersonId(@Param("personId") Long personId);
}