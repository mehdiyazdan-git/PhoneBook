package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("select (count(p) > 0) from Product p where p.createdBy = :createdBy or p.lastModifiedBy = :lastModifiedBy")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") Integer createdBy, @Param("lastModifiedBy") Integer lastModifiedBy);

}