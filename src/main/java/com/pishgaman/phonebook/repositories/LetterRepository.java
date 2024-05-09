package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.projections.LetterInfo;
import com.pishgaman.phonebook.projections.LetterProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    @Query("select count(l) from Letter l where l.company.id = :companyId and l.year.id = :yearId")
    Long countAllByCompanyIdAndYearId(@Param("companyId") Long companyId, @Param("yearId") Long yearId);
    @Query("select (count(l) > 0) from Letter l where l.createdBy = :createdBy or l.lastModifiedBy = :lastModifiedBy")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);

    @Query("select (count(l) > 0) from Letter l where l.year.id = :year_id")
    boolean existsByYearId(@Param("year_id") Long year_id);

    @Query("select count(l) from Letter l where l.year.id = :id")
    long countByYearId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "insert into public.letter\t" +
            "(creation_date, customer_id, company_id, content, letter_number, year_id, letter_state,letter_type_id)\n\t" +
            "values (:creationDate, :customerId, :companyId, :content, :letterNumber, :yearId, :letterState,:letterTypeId)"
            ,nativeQuery = true)
    void createLetter(@Param("creationDate") LocalDate creationDate,
                      @Param("customerId") Long customerId,
                      @Param("companyId") Long companyId,
                      @Param("content") String content,
                      @Param("letterNumber") String letterNumber,
                      @Param("yearId") Long yearId,
                      @Param("letterState") String letterState,
                      @Param("letterTypeId") Long letterTypeId
    );

            @Transactional
            @Modifying
            @Query(value = "update Letter  set " +
                    "creation_date = :creationDate, " +
                    "customer_id = :customerId, " +
                    "company_id = :companyId, " +
                    "content = :content, " +
                    "letter_number = :letterNumber, " +
                    "year_id = :yearId, " +
                    "letter_state = :letterState " +
                    "where id = :letterId",nativeQuery = true)
            public void updateLetter(
                    @Param("letterId") Long letterId,
                    @Param("creationDate") LocalDate creationDate,
                    @Param("customerId") Long customerId,
                    @Param("companyId") Long companyId,
                    @Param("content") String content,
                    @Param("letterNumber") String letterNumber,
                    @Param("yearId") Long yearId,
                    @Param("letterState") String letterState);


    @Transactional
    @Modifying
    @Query("update Letter l set l.letterState = :letterState where l.id = :id")
    void updateLetterState(@Param("letterState") LetterState letterState, @Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update Attachment a set a.deletable = :deletable where a.letter.id = :letterIdRef")
    void updateDeletable(@Param("deletable") boolean deletable, @Param("letterIdRef") Long letterIdRef);



    @Query("select (count(l) > 0) from Letter l where l.company = :sender")
    boolean existsAllBySender(@Param("sender") Sender sender);



    @Query(value = "SELECT l.id AS letterId, l.creationDate AS creationDate, l.content AS content, " +
            "l.letterNumber AS letterNumber, l.letterState AS letterState, " +
            "r.name AS recipientName, s.companyName AS senderName " +
            "FROM Letter l " +
            "LEFT JOIN l.customer r " +
            "LEFT JOIN l.company s",
            countQuery = "SELECT COUNT(l) FROM Letter l", // For pagination
            nativeQuery = false)
    Page<Letter> findAllWithProjection(Specification<Letter> spec, Pageable pageable);


    @Query("select l from Letter l")
    Page<LetterProjection> findAll(@Param("spec") Specification<Letter> spec, Pageable pageable);

    @Query("select (count(l) > 0) from Letter l where l.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
}