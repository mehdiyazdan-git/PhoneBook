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

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Query("select p from Person p where p.firstName like concat('%', :firstName, '%') or p.lastName like concat('%', :lastName, '%')")
    List<Person> findPersonByFirstNameOrLastNameContaining(@Param("firstName") String firstName, @Param("lastName") String lastName);


}
