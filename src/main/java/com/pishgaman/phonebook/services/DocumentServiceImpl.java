package com.pishgaman.phonebook.services;


import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.entities.Document;
import com.pishgaman.phonebook.mappers.DocumentMapper;
import com.pishgaman.phonebook.repositories.CompanyRepository;
import com.pishgaman.phonebook.repositories.DocumentRepository;
import com.pishgaman.phonebook.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;

    @Override
    public DocumentDto createDocument(DocumentDto documentDto) {
        if ( documentDto == null ) {
            return null;
        }

        Document document = new Document();
        if (documentDto.getPersonId() != null && documentDto.getPersonId() > 0) {
            document.setPerson( personRepository.findById( documentDto.getPersonId()).orElse(null));
        }
        if (documentDto.getCompanyId() != null && documentDto.getCompanyId() > 0) {
            document.setCompany( companyRepository.findById( documentDto.getCompanyId()).orElse(null));
        }
        document.setId( documentDto.getId() );
        document.setDocumentName( documentDto.getDocumentName());
        document.setDocumentType( documentDto.getDocumentType());
        document.setFileExtension( documentDto.getFileExtension());
        byte[] documentFile = documentDto.getDocumentFile();
        if ( documentFile != null ) {
            document.setDocumentFile( Arrays.copyOf( documentFile, documentFile.length ) );
        }
        return documentMapper.toDto(documentRepository.save(document));
    }

    @Override
    public List<DocumentDetailDto> findAllByPersonId(Long personId) {
        List<Object[]> documentsData = documentRepository.findAllDocumentsByPersonId(personId);
        return documentsData.stream()
                .map(data -> new DocumentDetailDto(
                        (Long) data[0], // id
                        (String) data[1], // documentName
                        (String) data[2], // documentType
                        (String) data[3] // fileExtension
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentDetailDto> findAllByCompanyId(Long companyId) {
        List<Object[]> documentsData = documentRepository.findAllDocumentsByCompanyId(companyId);
        return documentsData.stream()
                .map(data -> new DocumentDetailDto(
                        (Long) data[0], // id
                        (String) data[1], // documentName
                        (String) data[2], // documentType
                        (String) data[3] // fileExtension
                ))
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDto getDocumentById(Long id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        return documentOptional.map(documentMapper::toDto).orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    @Override
    public DocumentDto updateDocument(Long id, DocumentDto documentDto) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isEmpty()) {
            throw new RuntimeException("Document not found with id: " + id);
        }
        Document document = documentOptional.get();
        Document updatedDocument = documentMapper.partialUpdate(documentDto, document);
        return documentMapper.toDto(documentRepository.save(updatedDocument));
    }

    @Override
    public void deleteDocument(Long id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isEmpty()) {
            throw new RuntimeException("Document not found with id: " + id);
        }
        documentRepository.delete(documentOptional.get());
    }
}

