package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.enums.LetterState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    @Query("select count(l) from Letter l where l.year.id = :id")
    long countByYearId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "insert into public.letter\t" +
            "(creation_date, recipient_id, sender_id, content, letter_number, year_id, letter_state)\n\t" +
            "values (:creationDate, :recipientId, :senderId, :content, :letterNumber, :yearId, :letterState)"
            ,nativeQuery = true)
    void createLetter(@Param("creationDate") LocalDate creationDate,
                      @Param("recipientId") Long recipientId,
                      @Param("senderId") Long senderId,
                      @Param("content") String content,
                      @Param("letterNumber") String letterNumber,
                      @Param("yearId") Long yearId,
                      @Param("letterState") String letterState);

            @Transactional
            @Modifying
            @Query(value = "update Letter  set " +
                    "creation_date = :creationDate, " +
                    "recipient_id = :recipientId, " +
                    "sender_id = :senderId, " +
                    "content = :content, " +
                    "letter_number = :letterNumber, " +
                    "year_id = :yearId, " +
                    "letter_state = :letterState " +
                    "where id = :letterId",nativeQuery = true)
            public void updateLetter(
                    @Param("letterId") Long letterId,
                    @Param("creationDate") LocalDate creationDate,
                    @Param("recipientId") Long recipientId,
                    @Param("senderId") Long senderId,
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

    @Query("SELECT NEW com.pishgaman.phonebook.dtos.LetterDetailsDto(" +
            "l.id , " +
            "l.creationDate, " +
            "l.content, " +
            "l.letterNumber, " +
            "l.letterState, " +
            "r.name, " +
            "s.name) " +
            "FROM Letter l " +
            "LEFT JOIN l.recipient r " +
            "LEFT JOIN l.sender s")
    List<LetterDetailsDto> findLetterDetails();

    @Query("SELECT NEW com.pishgaman.phonebook.dtos.LetterDetailsDto(" +
            "l.id , " +
            "l.creationDate, " +
            "l.content, " +
            "l.letterNumber, " +
            "l.letterState, " +
            "r.name, " +
            "s.name) " +
            "FROM Letter l " +
            "LEFT JOIN l.recipient r " +
            "LEFT JOIN l.sender s " +
            "WHERE s.id is null or s.id = :senderId\t")
    List<LetterDetailsDto> findLetterDetailsBySenderId(@Param("senderId") Long senderId);

    @Query("select (count(l) > 0) from Letter l where l.sender = :sender")
    boolean existsAllBySender(@Param("sender") Sender sender);
}