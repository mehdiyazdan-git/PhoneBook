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

    @Transactional
    public BoardMemberDto createBoardMember(BoardMemberDto boardMemberDto) {
        BoardMember boardMember = new BoardMember();

        // Set the Person
        Person person = personRepository.findById(boardMemberDto.getPersonId())
                .orElseThrow(() -> new IllegalArgumentException("Person with id " + boardMemberDto.getPersonId() + " not found"));
        boardMember.setPerson(person);

        // Set the Position
        Position position = positionRepository.findById(boardMemberDto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("Position with id " + boardMemberDto.getPositionId() + " not found"));
        boardMember.setPosition(position);

        // Set the Company
        Company company = companyRepository.findById(boardMemberDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company with id " + boardMemberDto.getCompanyId() + " not found"));
        boardMember.setCompany(company);

        // Save the new BoardMember
        boardMember = boardMemberRepository.save(boardMember);
        return boardMemberMapper.toDto(boardMember);
    }

    @Transactional
    public BoardMemberDto updateBoardMember(Long id, BoardMemberDto boardMemberDto) {
        BoardMember boardMember = boardMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BoardMember with id " + id + " not found"));

        // Update the Person reference
        if (boardMemberDto.getPersonId() != null) {
            Person person = personRepository.findById(boardMemberDto.getPersonId())
                    .orElseThrow(() -> new IllegalArgumentException("Person with id " + boardMemberDto.getPersonId() + " not found"));
            boardMember.setPerson(person);
        }

        // Update the Position reference
        if (boardMemberDto.getPositionId() != null) {
            Position position = positionRepository.findById(boardMemberDto.getPositionId())
                    .orElseThrow(() -> new IllegalArgumentException("Position with id " + boardMemberDto.getPositionId() + " not found"));
            boardMember.setPosition(position);
        }

        // Update the Company reference
        if (boardMemberDto.getCompanyId() != null) {
            Company company = companyRepository.findById(boardMemberDto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company with id " + boardMemberDto.getCompanyId() + " not found"));
            boardMember.setCompany(company);
        }

        // Save the updated BoardMember
        boardMember = boardMemberRepository.save(boardMember);
        return boardMemberMapper.toDto(boardMember);
    }

    public void deleteBoardMember(Long id) {
        boardMemberRepository.deleteById(id);
    }

    public BoardMemberDto getBoardMemberById(Long id) {
        BoardMember boardMember = boardMemberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company with id " + id + " not found"));
        return boardMemberMapper.toDto(boardMember);
    }

}
