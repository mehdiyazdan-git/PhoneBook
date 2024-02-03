package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }
    @PostMapping(path = {"/",""})
    public ResponseEntity<?> createPerson(@RequestBody PersonDto personDto) {
        personService.createPerson(personDto);
        return new ResponseEntity<>( HttpStatus.CREATED);
    }

    @GetMapping(path = {"/",""})
    public ResponseEntity<List<PersonDto>> getAllPersons() {
        List<PersonDto> persons = personService.findAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }
    @GetMapping(path = "all-by-recipient-id/{recipientId}")
    public ResponseEntity<List<PersonDto>> getAllByRecipientId(@PathVariable("recipientId") Long recipientId) {
        List<PersonDto> persons = personService.getAllByRecipientId(recipientId);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getPersonById(@PathVariable Long id) {
        PersonDto person = personService.findById(id);
        if (person != null) {
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable Long id, @RequestBody PersonDto personDto) {
        personService.updatePerson(id,personDto);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
