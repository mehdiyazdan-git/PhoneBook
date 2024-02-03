package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.mappers.PersonMapper;
import com.pishgaman.phonebook.repositories.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final RecipientService recipientService;
    @Autowired
    public PersonService(PersonRepository personRepository, PersonMapper personMapper, RecipientService recipientService) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.recipientService = recipientService;
    }
    public List<PersonDto> findAll(){
        return personRepository.findAll().stream().map(personMapper::toDto).collect(Collectors.toList());
    }
    public List<PersonDto> getAllByRecipientId(Long recipientId){
        return personRepository.getAllByRecipientId(recipientId).stream().map(personMapper::toDto).collect(Collectors.toList());
    }
    public PersonDto findById(Long personId){
        return personMapper.toDto(getPersonById(personId));
    }

    private Person getPersonById(Long personId) {
        Optional<Person> optionalPerson = personRepository.findById(personId);
        if (optionalPerson.isEmpty()) throw new EntityNotFoundException("شخص با شناسه " + personId + " یافت نشد.");
        return optionalPerson.get();
    }

    public void createPerson(PersonDto persondto){

        personRepository.createPerson(
                persondto.getEmail(),
                persondto.getFirstName(),
                persondto.getLastName(),
                persondto.getMobile(),
                persondto.getRecipientId()
        );

    }
    @Transactional
    public void updatePerson(Long personId,PersonDto personDto){
        if (!personRepository.existsById(personId)) throw new IllegalArgumentException("شخص با شناسه " + personId + " یافت نشد.");
        if (!recipientService.existById(personDto.getRecipientId())) throw new IllegalArgumentException("گیرنده با شناسه " + personDto.getRecipientId() + " یافت نشد.");
        try {
            personRepository.updatePerson(
                    personId,
                    personDto.getEmail(),
                    personDto.getFirstName(),
                    personDto.getLastName(),
                    personDto.getMobile(),
                    personDto.getRecipientId()
            );
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
    public void deletePerson(Long personId){
        Person personById = getPersonById(personId);
        personRepository.delete(personById);
    }
}
