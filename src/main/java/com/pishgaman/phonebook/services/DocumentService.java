package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.projections.DocumentInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface DocumentService {
    DocumentDto createDocument(DocumentDto documentDto);
    List<DocumentDetailDto> findAllByPersonId(Long personId);
    List<DocumentDetailDto> findAllByCompanyId(Long companyId);
    List<DocumentDetailDto> findAllByLetterId(Long letterId);
    DocumentDto getDocumentById(Long id);

    byte[] generateDocumentTemplate() throws IOException;

    String importDocumentsFromExcel(MultipartFile file) throws IOException;

    DocumentDto updateDocument(Long id, DocumentDto documentDto);
    void deleteDocument(Long id);
}
