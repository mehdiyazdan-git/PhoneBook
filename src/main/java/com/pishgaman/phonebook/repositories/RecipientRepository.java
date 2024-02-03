package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Recipient;
import com.pishgaman.phonebook.entities.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    @Query("select r from Recipient r where r.name = :name")
    Recipient findRecipientByName(@Param("name") String name);

    @Query(value = "select count(l.id) > 0 from letter l where recipient_id = :recipientId", nativeQuery = true)
    boolean hasAssociatedLetter(Long recipientId);

    @Query("select r from Recipient r where r.name like concat('%', :recipientName, '%')")
    List<Recipient> findByRecipientNameContains(@Param("recipientName") String recipientName);
}