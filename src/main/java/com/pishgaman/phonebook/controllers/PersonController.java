package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.PersonSelectDto;
import com.pishgaman.phonebook.searchforms.PersonSearch;
import com.pishgaman.phonebook.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/download-all-persons.xlsx")
    public ResponseEntity<byte[]> downloadAllPersonsExcel() throws IOException {
        byte[] excelData = personService.generateAllPersonsExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_persons.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<PersonDto>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            PersonSearch search) {
        Page<PersonDto> persons = personService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonSelectDto>> findPersonByFirstNameOrLastNameContaining(@RequestParam String searchParam) {
        List<PersonSelectDto> persons = personService.findPersonByFirstNameOrLastNameContaining(searchParam);
        return ResponseEntity.ok(persons);
    }
    @GetMapping("/select")
    public ResponseEntity<List<PersonSelectDto>> getPersonSelect() {
        List<PersonSelectDto> persons = personService.getPersonSelect();
        return ResponseEntity.ok(persons);
    }


    @GetMapping("/{personId}")
    public ResponseEntity<PersonDto> getPersonById(@PathVariable Long personId) {
        PersonDto person = personService.findById(personId);
        return ResponseEntity.ok(person);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) {
        PersonDto createdPerson = personService.createPerson(personDto);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{personId}")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable("personId") Long personId, @RequestBody PersonDto personDto) {
        PersonDto updatedPerson = personService.updatePerson(personId, personDto);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity<String> deletePerson(@PathVariable("personId") Long personId) {
        try {
            String message = personService.removePerson(personId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
