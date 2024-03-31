package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.projections.DocumentInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocumentService {
    DocumentDto createDocument(DocumentDto documentDto);
    List<DocumentDetailDto> findAllByPersonId(Long personId);
    List<DocumentDetailDto> findAllByCompanyId(Long companyId);
    DocumentDto getDocumentById(Long id);
    DocumentDto updateDocument(Long id, DocumentDto documentDto);
    void deleteDocument(Long id);
}
