package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterTypeRepository extends JpaRepository<LetterType, Long> {
    @Query("select (count(l) > 0) from LetterType l where l.createdBy = :createdBy or l.lastModifiedBy = :lastModifiedBy")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);
}