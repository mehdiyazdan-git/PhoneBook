package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.LetterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterTypeRepository extends JpaRepository<LetterType, Long> {
}