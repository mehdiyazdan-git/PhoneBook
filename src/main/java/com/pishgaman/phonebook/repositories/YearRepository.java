package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YearRepository extends JpaRepository<Year, Long>, JpaSpecificationExecutor<Year> {
    @Query("select y from Year y where y.name = :year")
    Optional<Year> findByYearName(@Param("year") Long year);

    @Query("select y from Year y where y.name = :name")
    Optional<Year> findYearByName(@Param("name") Long name);

    @Query("select y from Year y where y.name = :name and y.id <> :id")
    Optional<Year> findYearByNameAndIdNot(@Param("name") Long name, @Param("id") Long id);

}