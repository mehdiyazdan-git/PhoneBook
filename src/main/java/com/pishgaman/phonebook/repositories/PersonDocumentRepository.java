package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.PersonDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonDocumentRepository extends JpaRepository<PersonDocument, Long> {
    Optional<PersonDocument> findByPersonId(Long personId);
}