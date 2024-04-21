package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.BoardMemberDetailsDto;
import com.pishgaman.phonebook.dtos.BoardMemberDto;
import com.pishgaman.phonebook.dtos.BoardMemberPDFDto;
import com.pishgaman.phonebook.exceptions.BoardMemberAlreadyExistsException;
import com.pishgaman.phonebook.searchforms.BoardMemberSearch;
import com.pishgaman.phonebook.services.BoardMemberService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    //generate findAllByPersonId method
    @GetMapping("/find-all-by-person-id/{personId}")
    public ResponseEntity<List<BoardMemberDetailsDto>> getAllBoardMembersByPersonId(@PathVariable Long personId){
        List<BoardMemberDetailsDto> boardMembers = boardMemberService.findAllByPersonId(personId);
        return ResponseEntity.ok(boardMembers);
    }


    @GetMapping(value = "/pdf/{personId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> generatePDF(@PathVariable Long personId) {
        try {
            byte[] pdfContent = boardMemberService.generateBoardMemberPDFByPersonId(personId);
            ByteArrayResource resource = new ByteArrayResource(pdfContent);

            HttpHeaders headers = new HttpHeaders();
            headers.add(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=board_member_" + personId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfContent.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IllegalAccessException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download-all-boardmembers.xlsx")
    public ResponseEntity<byte[]> downloadAllBoardMembersExcel() throws IOException {
        byte[] excelData = boardMemberService.exportBoardMembersToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_board_members.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import-board-members")
    public ResponseEntity<String> importBoardMembersFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            String message = boardMemberService.importBoardMembersFromExcel(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import board members from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadBoardMemberTemplate() {
        try {
            byte[] templateBytes = boardMemberService.generateBoardMemberTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "board_member_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
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
