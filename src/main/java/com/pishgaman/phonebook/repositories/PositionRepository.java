package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {

    @Query("select p from Position p where p.name = :name")
    Position findPositionByName(@Param("name") String name);

    @Query("select p from Position p where p.name like concat('%', :name, '%')")
    List<Position> findAllByNameContaining(@Param("name") String name);
}
