package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.services.BoardMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/board-members")
public class BoardMemberController {

    private final BoardMemberService boardMemberService;

    @Autowired
    public BoardMemberController(BoardMemberService boardMemberService) {
        this.boardMemberService = boardMemberService;
    }

    @GetMapping(path = {"/",""})
    public ResponseEntity<List<BoardMemberDetailsDto>> getAllBoardMembers() {
        List<BoardMemberDetailsDto> boardMembers = boardMemberService.getAllBoardMembers();
        return ResponseEntity.ok(boardMembers);
    }
    @GetMapping(path = "/all-by-companyId/{companyId}")
    public ResponseEntity<List<BoardMemberDetailsDto>> findAllByCompanyId(@PathVariable( name = "companyId") Long companyId) {

        List<BoardMemberDetailsDto> boardMembers = boardMemberService.findAllByCompanyId(companyId);
        return ResponseEntity.ok(boardMembers);
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<BoardMemberDto> createBoardMember(@RequestBody BoardMemberDto boardMemberDto) {
        try {
            BoardMemberDto createdBoardMember = boardMemberService.createBoardMember(boardMemberDto);
            return new ResponseEntity<>(createdBoardMember, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardMemberDto> updateBoardMember(@PathVariable Long id, @RequestBody BoardMemberDto boardMemberDto) {
        try {
            BoardMemberDto updatedBoardMember = boardMemberService.updateBoardMember(id, boardMemberDto);
            return ResponseEntity.ok(updatedBoardMember);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardMember(@PathVariable Long id) {
        boardMemberService.deleteBoardMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardMemberDto> getBoardMemberById(@PathVariable Long id) {
        BoardMemberDto boardMember = boardMemberService.getBoardMemberById(id);
        if (boardMember != null) {
            return ResponseEntity.ok(boardMember);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
