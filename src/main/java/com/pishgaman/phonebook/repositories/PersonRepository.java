package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Transactional
    @Modifying
    @Query(value = "insert into public.person (email, first_name, last_name, mobile, recipient_id)\n" +
            "values ( :email, :firstName, :lastName, :mobile, :recipientId);"
            ,nativeQuery = true)
    void createPerson(@Param("email") String email,
                      @Param("firstName") String firstName,
                      @Param("lastName") String lastName,
                      @Param("mobile") String mobile,
                      @Param("recipientId") Long recipientId);

    @Transactional
    @Modifying
    @Query(value = "update person  set " +
            "email = :email, " +
            "first_name = :firstName, " +
            "last_name = :lastName, " +
            "mobile = :mobile, " +
            "recipient_id = :recipientId " +
            "where id = :personId ",nativeQuery = true)
    public void updatePerson(
            @Param("personId") Long personId,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("mobile") String mobile,
            @Param("recipientId") Long recipientId);


    @Query("select p from Person p where p.recipient.id = :recipient_id")
    List<Person> getAllByRecipientId(@Param("recipient_id") Long recipient_id);

}
