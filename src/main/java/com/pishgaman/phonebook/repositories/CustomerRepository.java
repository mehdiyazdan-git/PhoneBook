package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

//    @Query("select c from customer c where c.name = :name")
    Customer findCustomerByName(@Param("name") String name);

    @Query("select c from Customer c where c.name = :name and c.id <> :id")
    Customer findCustomerByNameAndIdNot(@Param("name") String name, @Param("id") Long id);


//    @Query("select c from customer c where c.name like concat('%', :customerName, '%')")
    List<Customer> findCustomerByNameContains(@Param("customerName") String customerName);
    @Query(value = "select count(l.id) > 0 from letter l where customer_id = :customerId", nativeQuery = true)
    boolean hasAssociatedLetter(Long customerId);
}