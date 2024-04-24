package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Query("select p from Person p where p.firstName like concat('%', :firstName, '%') or p.lastName like concat('%', :lastName, '%')")
    List<Person> findPersonByFirstNameOrLastNameContaining(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("select p from Person p where p.nationalId = :nationalId")
    Optional<Person> findPersonByNationalId(@Param("nationalId") String nationalId);

    @Query("select (count(p) > 0) from Person p where p.createdBy = :createdBy or p.lastModifiedBy = :lastModifiedBy")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);


}
