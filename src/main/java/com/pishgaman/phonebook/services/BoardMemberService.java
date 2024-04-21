package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.dtos.BoardMemberPDFDto;
import com.pishgaman.phonebook.dtos.CompanyDto;
import com.pishgaman.phonebook.entities.BoardMember;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.exceptions.BoardMemberAlreadyExistsException;
import com.pishgaman.phonebook.mappers.BoardMemberDetailsMapper;
import com.pishgaman.phonebook.mappers.BoardMemberMapper;
import com.pishgaman.phonebook.mappers.BoardMemberPDFMapper;
import com.pishgaman.phonebook.repositories.BoardMemberRepository;
import com.pishgaman.phonebook.repositories.CompanyRepository;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.repositories.PositionRepository;
import com.pishgaman.phonebook.searchforms.BoardMemberSearch;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.BoardMemberSpecification;
import com.pishgaman.phonebook.utils.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardMemberService {
    private final BoardMemberRepository boardMemberRepository;
    private final BoardMemberMapper boardMemberMapper;
    private final BoardMemberDetailsMapper boardMemberDetailsMapper;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final BoardMemberPDFMapper boardMemberPDFMapper;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

    public Page<BoardMemberDetailsDto> findAll(int page, int size, String sortBy, String order, BoardMemberSearch search) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(order), Objects.equals(sortBy, "fullName") ? "personFirstName" : sortBy);
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Specification<BoardMember> specification = BoardMemberSpecification.getSpecification(search);
            return boardMemberRepository.findAll(specification, pageRequest)
                    .map(boardMemberDetailsMapper::toDto);
        } catch (Exception e) {
            System.out.println("BoardMemberService Exception: " + e.getMessage());
            throw e;
        }
    }

    public BoardMemberDto findById(Long boardMemberId) {
        try {
            Optional<BoardMember> optionalBoardMember = boardMemberRepository.findById(boardMemberId);
            if (optionalBoardMember.isEmpty()) {
                throw new EntityNotFoundException("عضو هیئت مدیره با شناسه: " + boardMemberId + " یافت نشد.");
            }
            BoardMemberDto dto = boardMemberMapper.toDto(optionalBoardMember.get());
            dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
            dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
            dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
            dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public List<BoardMemberDetailsDto> findAllByPersonId(Long personId) {
        return boardMemberRepository.findAllByPersonId(personId)
                .stream()
                .map(boardMemberDetailsMapper::toDto)
                .collect(Collectors.toList());
    }
    public byte [] generateBoardMemberPDFByPersonId(Long personId) throws IllegalAccessException, IOException {
        List<BoardMemberPDFDto> boardMemberDetailsDtos = findAllByPersonIdForPDFGeneration(personId);
        return PDFDataExporter.exportData(boardMemberDetailsDtos, BoardMemberPDFDto.class);
    }
    public List<BoardMemberPDFDto> getBoardMemberPDFByPersonId1(Long personId) throws IllegalAccessException {
        return findAllByPersonIdForPDFGeneration(personId);
    }

    private List<BoardMemberPDFDto> findAllByPersonIdForPDFGeneration(Long personId) {
       return boardMemberRepository
               .findAllByPersonId(personId)
               .stream()
               .map(boardMemberPDFMapper::toDto)
               .collect(Collectors.toList());
    }

    public String importBoardMembersFromExcel(MultipartFile file) throws IOException {
        List<BoardMemberDto> boardMemberDtos = ExcelDataImporter.importData(file, BoardMemberDto.class);
        List<BoardMember> boardMembers = boardMemberDtos.stream().map(boardMemberMapper::toEntity).collect(Collectors.toList());
        boardMemberRepository.saveAll(boardMembers);
        return boardMembers.size() + " board members have been imported successfully.";
    }

    public byte[] exportBoardMembersToExcel() throws IOException {
        List<BoardMemberDto> boardMemberDtos = boardMemberRepository.findAll().stream().map(boardMemberMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(boardMemberDtos, BoardMemberDto.class);
    }

    public byte[] generateBoardMemberTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(BoardMemberDto.class);
    }
    public BoardMemberDto createBoardMember(BoardMemberDto boardMemberDto) {
        try {
            BoardMember existingBoardMemberWithPerson = boardMemberRepository.findByPersonIdAndCompanyIdAndPositionId(boardMemberDto.getPersonId(), boardMemberDto.getCompanyId(), boardMemberDto.getPositionId());
            if (existingBoardMemberWithPerson != null) {
                throw new BoardMemberAlreadyExistsException("این شخص قبلا در این شرکت و در این سمت حضور دارد.");
            }
            BoardMember existingBoardMember = boardMemberRepository.findByCompanyIdAndPositionId(boardMemberDto.getCompanyId(), boardMemberDto.getPositionId());
            if (existingBoardMember != null) {
                throw new BoardMemberAlreadyExistsException("این سمت در شرکت انتخاب شده قبلا اشغال شده است.");
            }

            BoardMember entity = new BoardMember();
            entity.setPerson(personRepository.findById(boardMemberDto.getPersonId()).orElse(null));
            entity.setCompany(companyRepository.findById(boardMemberDto.getCompanyId()).orElse(null));
            entity.setPosition(positionRepository.findById(boardMemberDto.getPositionId()).orElse(null));
            BoardMember saved = boardMemberRepository.save(entity);
            return boardMemberMapper.toDto(saved);
        } catch (Exception e) {
            System.out.println("BoardMemberService Exception: " + e.getMessage());
            throw e;
        }
    }
    public BoardMemberDto updateBoardMember(Long boardMemberId, BoardMemberDto boardMemberDto) {
        try {
            BoardMember existingBoardMemberWithPerson = boardMemberRepository.findByPersonIdAndCompanyIdAndPositionIdAndNotBoardMemberId(boardMemberDto.getPersonId(), boardMemberDto.getCompanyId(), boardMemberDto.getPositionId(), boardMemberId);
            if (existingBoardMemberWithPerson != null) {
                throw new BoardMemberAlreadyExistsException("این شخص قبلا در این شرکت و در این سمت حضور دارد.");
            }
            BoardMember existingBoardMember = boardMemberRepository.findByCompanyIdAndPositionIdAndNotBoardMemberId(boardMemberDto.getCompanyId(), boardMemberDto.getPositionId(), boardMemberId);
            if (existingBoardMember != null) {
                throw new BoardMemberAlreadyExistsException("این سمت در شرکت انتخاب شده قبلا اشغال شده است.");
            }

            Optional<BoardMember> optionalBoardMember = boardMemberRepository.findById(boardMemberId);
            if (optionalBoardMember.isEmpty()) {
                throw new EntityNotFoundException("عضو هیئت مدیره با شناسه: " + boardMemberId + " یافت نشد.");
            }
            BoardMember boardMember = optionalBoardMember.get();

            boardMember.setPerson(personRepository.findById(boardMemberDto.getPersonId()).orElse(null));
            boardMember.setCompany(companyRepository.findById(boardMemberDto.getCompanyId()).orElse(null));
            boardMember.setPosition(positionRepository.findById(boardMemberDto.getPositionId()).orElse(null));
            BoardMember updated = boardMemberRepository.save(boardMember);
            return boardMemberMapper.toDto(updated);
        } catch (Exception e) {
            System.out.println("BoardMemberService Exception: " + e.getMessage());
            throw e;
        }
    }

    public void deleteBoardMember(Long boardMemberId) {
        try {
            if (!boardMemberRepository.existsById(boardMemberId)) {
                throw new EntityNotFoundException("عضو هیئت مدیره با شناسه: " + boardMemberId + " یافت نشد.");
            }
            boardMemberRepository.deleteById(boardMemberId);
        } catch (Exception e) {
            System.out.println("BoardMemberService Exception: " + e.getMessage());
            throw e;
        }
    }
}
