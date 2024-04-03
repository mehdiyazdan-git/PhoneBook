package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.entities.BoardMember;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.mappers.BoardMemberDetailsMapper;
import com.pishgaman.phonebook.mappers.BoardMemberMapper;
import com.pishgaman.phonebook.repositories.BoardMemberRepository;
import com.pishgaman.phonebook.repositories.CompanyRepository;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.repositories.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardMemberService {
    private final BoardMemberRepository boardMemberRepository;
    private final PersonRepository personRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final BoardMemberMapper boardMemberMapper;
    private final BoardMemberDetailsMapper boardMemberDetailsMapper;

    public List<BoardMemberDetailsDto> getAllBoardMembers() {
        List<BoardMember> boardMembers = boardMemberRepository.findAll();
        return boardMembers.stream()
                .map(boardMemberDetailsMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BoardMemberDetailsDto> findAllByCompanyId(Long companyId) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        Company company = companyOptional.orElseThrow(() -> new IllegalArgumentException("شرکت با شناسه " + companyId + " یافت نشد"));

        List<BoardMember> boardMembers = boardMemberRepository.findAllByCompanyId(companyId);
        return boardMembers.stream()
                .map(boardMemberDetailsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BoardMemberDto createBoardMember(BoardMemberDto boardMemberDto) {

        // Check if the position is unique for the person in the company
        if (isPositionUniqueForPersonInCompany(boardMemberDto.getPersonId(), boardMemberDto.getCompanyId(), boardMemberDto.getPositionId())) {
            throw new IllegalArgumentException("این پست برای این شخص در این شرکت از قبل اختصاص داده شده است");
        }
        // Check if the position is unique for the company
        if (isPositionUniqueForCompany(boardMemberDto.getCompanyId(), boardMemberDto.getPositionId())) {
            throw new IllegalArgumentException("پست باید در هر شرکت منحصر به فرد باشد");
        }

        BoardMember boardMember = new BoardMember();

        // Set the Person
        Person person = personRepository.findById(boardMemberDto.getPersonId())
                .orElseThrow(() -> new IllegalArgumentException("شخص با شناسه " + boardMemberDto.getPersonId() + " یافت نشد"));
        boardMember.setPerson(person);

        // Set the Position
        Position position = positionRepository.findById(boardMemberDto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("پست با شناسه " + boardMemberDto.getPositionId() + " یافت نشد"));
        boardMember.setPosition(position);

        // Set the Company
        Company company = companyRepository.findById(boardMemberDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("شرکت با شناسه " + boardMemberDto.getCompanyId() + " یافت نشد"));
        boardMember.setCompany(company);

        // Save the new BoardMember
        boardMember = boardMemberRepository.save(boardMember);
        return boardMemberMapper.toDto(boardMember);
    }


    @Transactional
    public BoardMemberDto updateBoardMember(Long id, BoardMemberDto boardMemberDto) {
        BoardMember boardMember = boardMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("عضو هیئت مدیره با شناسه " + id + " یافت نشد"));

        // Check if the position is unique for the person in the company, excluding the current board member
        if (boardMemberRepository.findByPersonIdAndCompanyIdAndPositionIdAndNotBoardMemberId(boardMemberDto.getPersonId(), boardMemberDto.getCompanyId(), boardMemberDto.getPositionId(), id) != null) {
            throw new IllegalArgumentException("این پست برای این شخص در این شرکت از قبل اختصاص داده شده است");
        }

        // Check if the position is unique for the company, excluding the current board member
        if (boardMemberRepository.findByCompanyIdAndPositionIdAndNotBoardMemberId(boardMemberDto.getCompanyId(), boardMemberDto.getPositionId(), id) != null) {
            throw new IllegalArgumentException("پست باید در هر شرکت منحصر به فرد باشد");
        }

        // Update the Person reference
        if (boardMemberDto.getPersonId() != null) {
            Person person = personRepository.findById(boardMemberDto.getPersonId())
                    .orElseThrow(() -> new IllegalArgumentException("شخص با شناسه " + boardMemberDto.getPersonId() + " یافت نشد"));
            boardMember.setPerson(person);
        }

        // Update the Position reference
        if (boardMemberDto.getPositionId() != null) {
            Position position = positionRepository.findById(boardMemberDto.getPositionId())
                    .orElseThrow(() -> new IllegalArgumentException("پست با شناسه " + boardMemberDto.getPositionId() + " یافت نشد"));
            boardMember.setPosition(position);
        }

        // Update the Company reference
        if (boardMemberDto.getCompanyId() != null) {
            Company company = companyRepository.findById(boardMemberDto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("شرکت با شناسه " + boardMemberDto.getCompanyId() + " یافت نشد"));
            boardMember.setCompany(company);
        }

        // Save the updated BoardMember
        boardMember = boardMemberRepository.save(boardMember);
        return boardMemberMapper.toDto(boardMember);
    }


    private boolean isPositionUniqueForCompany(Long companyId, Long positionId) {
        // Check if there is already a board member with the same position for the same company
        return boardMemberRepository.findByCompanyIdAndPositionId(companyId, positionId) != null;
    }

    private boolean isPositionUniqueForPersonInCompany(Long personId, Long companyId, Long positionId) {
        // Check if there is already a board member with the same person and position in the company
        return boardMemberRepository.findByPersonIdAndCompanyIdAndPositionId(personId, companyId, positionId) != null;
    }

    public void deleteBoardMember(Long id) {
        boardMemberRepository.deleteById(id);
    }

    public BoardMemberDto getBoardMemberById(Long id) {
        BoardMember boardMember = boardMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("شرکت با شناسه " + id + " یافت نشد"));
        return boardMemberMapper.toDto(boardMember);
    }

}

