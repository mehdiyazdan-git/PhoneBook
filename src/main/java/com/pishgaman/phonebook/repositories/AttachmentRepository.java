package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.dtos.AttachListDto;
import com.pishgaman.phonebook.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query(value = " select new com.pishgaman.phonebook.dtos.AttachListDto(a.id, a.fileName, a.fileType,a.deletable) from Attachment a where a.letter.id = :letterId")
    List<AttachListDto> findAllByLetterId(@Param("letterId") Long letterId);




}