package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SenderRepository extends JpaRepository<Sender, Long> {
    @Query("select s from Sender s where s.name = :name")
    Sender findSenderByName(@Param("name") String name);

    @Query(value = "select count(l.id) > 0 from letter l where sender_id = :senderId",nativeQuery = true)
    boolean hasAssociatedLetter(Long senderId);

    @Query("select s from Sender s where s.name like concat('%', :senderName, '%')")
    List<Sender> findBySenderNameContains(@Param("senderName") String senderName);

    @Query(value = "SELECT MAX(letter_counter) AS max_letter_counter FROM public.sender WHERE id = :senderId",nativeQuery = true)
    Integer getMaxLetterCountBySenderId(Long senderId);

    @Modifying
    @Query(value = "UPDATE public.sender SET letter_counter = :count WHERE id = :senderId",nativeQuery = true)
    void incrementLetterCountByOne(Integer count,Long senderId);

    @Query("select (count(s) > 0) from Sender s where s.createdBy = :createdBy or s.lastModifiedBy = :lastModifiedBy")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);
}