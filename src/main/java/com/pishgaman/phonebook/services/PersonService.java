package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.PersonSelectDto;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.mappers.PersonMapper;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.searchforms.PersonSearch;
import com.pishgaman.phonebook.specifications.PersonSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;


    public Page<PersonDto> findAll(PersonSearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Person> specification = PersonSpecification.getSpecification(search);
        return personRepository.findAll(specification, pageRequest)
                .map(personMapper::toDto);
    }
    public List<PersonSelectDto> findPersonByFirstNameOrLastNameContaining(String searchParam) {
        List<Person> persons = personRepository.findPersonByFirstNameOrLastNameContaining(searchParam, searchParam);
        return persons.stream()
                .map(person -> new PersonSelectDto(person.getId(), person.getFirstName() + " " + person.getLastName()))
                .collect(Collectors.toList());
    }
    public List<PersonSelectDto> getPersonSelect() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(person -> new PersonSelectDto(person.getId(), person.getFirstName() + " " + person.getLastName()))
                .collect(Collectors.toList());
    }

    private Person findPersonById(Long personId){
        Optional<Person> optionalPerson = personRepository.findById(personId);
        if (optionalPerson.isEmpty()) throw new EntityNotFoundException("فرستنده با شناسه : " + personId + " یافت نشد.");
        return optionalPerson.get();
    }

    public PersonDto findById(Long personId){
        return personMapper.toDto(findPersonById(personId));
    }

    public PersonDto createPerson(PersonDto personDto){
        Person entity = personMapper.toEntity(personDto);
        Person saved = personRepository.save(entity);
        return personMapper.toDto(saved);
    }

    public PersonDto updatePerson(Long personId, PersonDto personDto) {
        Person personById = findPersonById(personId);
        Person personToBeUpdate = personMapper.partialUpdate(personDto, personById);
        Person updated = personRepository.save(personToBeUpdate);
        return personMapper.toDto(updated);
    }

    public String removePerson(Long personId){
        boolean result = personRepository.existsById(personId);
        if (result){
            personRepository.deleteById(personId);
            return "فرستنده با موفقیت حذف شد. ";
        }
        return "خطا در حذف فرستنده.";
    }
}
