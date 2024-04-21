package com.pishgaman.phonebook.services;


import com.pishgaman.phonebook.dtos.DocumentDetailDto;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.entities.Document;
import com.pishgaman.phonebook.mappers.DocumentMapper;
import com.pishgaman.phonebook.repositories.CompanyRepository;
import com.pishgaman.phonebook.repositories.DocumentRepository;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.utils.DateConvertor;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelDataImporter;
import com.pishgaman.phonebook.utils.ExcelTemplateGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final LetterRepository letterRepository;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

    public DocumentDto findById(Long documentId){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        DocumentDto dto = documentMapper.toDto(document);
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
    }

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
        if (documentDto.getLetterId() != null && documentDto.getLetterId() > 0) {
            document.setLetter( letterRepository.findById( documentDto.getLetterId()).orElse(null));
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
    public List<DocumentDetailDto> findAllByLetterId(Long letterId) {
        List<Object[]> documentsData = documentRepository.findAllDocumentsByLetterId(letterId);
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
    public byte[] generateDocumentTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(DocumentDto.class);
    }
    @Override
    public String importDocumentsFromExcel(MultipartFile file) throws IOException {
        List<DocumentDto> documentDtos = ExcelDataImporter.importData(file, DocumentDto.class);
        List<Document> documents = documentDtos.stream().map(documentMapper::toEntity).collect(Collectors.toList());
        documentRepository.saveAll(documents);
        return documents.size() + " documents have been imported successfully.";
    }

    public byte[] exportDocumentsToExcel() throws IOException {
        List<DocumentDto> documentDtos = documentRepository.findAll().stream().map(documentMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(documentDtos, DocumentDto.class);
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

