package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.exceptions.BoardMemberAlreadyExistsException;
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
    public ResponseEntity<?> createBoardMember(@RequestBody BoardMemberDto boardMemberDto) {
        try {
            BoardMemberDto createdBoardMember = boardMemberService.createBoardMember(boardMemberDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBoardMember);
        } catch (BoardMemberAlreadyExistsException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("مشکلی در سرور رخ داده است");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoardMember(@PathVariable Long id, @RequestBody BoardMemberDto boardMemberDto) {
        try {
            BoardMemberDto updatedBoardMember = boardMemberService.updateBoardMember(id, boardMemberDto);
            return ResponseEntity.ok(updatedBoardMember);
        } catch (BoardMemberAlreadyExistsException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("مشکلی در سرور رخ داده است");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoardMember(@PathVariable Long id) {
        try {
            boardMemberService.deleteBoardMember(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("مشکلی در سرور رخ داده است");
        }
    }
}
