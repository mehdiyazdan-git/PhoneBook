package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.searchforms.BoardMemberSearch;
import com.pishgaman.phonebook.services.BoardMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/api/board-members")
@RequiredArgsConstructor
public class BoardMemberController {

    private final BoardMemberService boardMemberService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<BoardMemberDetailsDto>> getAllBoardMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order, BoardMemberSearch search) {
        Page<BoardMemberDetailsDto> boardMembers = boardMemberService.findAll(page, size, sortBy, order, search);
        return ResponseEntity.ok(boardMembers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardMemberDto> getBoardMemberById(@PathVariable Long id) {
        BoardMemberDto boardMember = boardMemberService.findById(id);
        return ResponseEntity.ok(boardMember);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<BoardMemberDto> createBoardMember(@RequestBody BoardMemberDto boardMemberDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardMemberService.createBoardMember(boardMemberDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardMemberDto> updateBoardMember(@PathVariable Long id, @RequestBody BoardMemberDto boardMemberDto) {
        try {
            BoardMemberDto updatedBoardMember = boardMemberService.updateBoardMember(id, boardMemberDto);
            return ResponseEntity.ok(updatedBoardMember);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Position is already occupied in this company.");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board member with ID: " + id + " not found.");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardMember(@PathVariable Long id) {
        try {
            boardMemberService.deleteBoardMember(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }
}
