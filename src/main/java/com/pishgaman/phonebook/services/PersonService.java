package com.pishgaman.phonebook.services;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.PersonSelectDto;
import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.exceptions.DatabaseIntegrityViolationException;
import com.pishgaman.phonebook.exceptions.DuplicateNationalIdException;
import com.pishgaman.phonebook.mappers.PersonMapper;
import com.pishgaman.phonebook.repositories.BoardMemberRepository;
import com.pishgaman.phonebook.repositories.DocumentRepository;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.repositories.ShareholderRepository;
import com.pishgaman.phonebook.searchforms.PersonSearch;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.PersonSpecification;
import com.pishgaman.phonebook.specifications.PositionSpecification;
import com.pishgaman.phonebook.utils.DateConvertor;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelDataImporter;
import com.pishgaman.phonebook.utils.ExcelTemplateGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final DocumentRepository documentRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ShareholderRepository shareholderRepository;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }


    public Page<PersonDto> findAll(PersonSearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Person> specification = PersonSpecification.getSpecification(search);
        return personRepository.findAll(specification, pageRequest)
                .map(personMapper::toDto);
    }
    public List<PersonSelectDto> findAllPersonSelect(String searchParam) {
        Specification<Person> specification = PersonSpecification.getSelectSpecification(searchParam);
        return personRepository.findAll(specification).stream().map(personMapper::toSelectDto).collect(Collectors.toList());
    }
    public List<PersonSelectDto> searchPersonByNameContaining(String searchQuery) {
        return personRepository.findPersonByFirstNameOrLastNameContaining(searchQuery,searchQuery).stream().map(personMapper::toSelectDto).collect(Collectors.toList());
    }
    public String importPersonsFromExcel(MultipartFile file) throws IOException {
        List<PersonDto> personDtos = ExcelDataImporter.importData(file, PersonDto.class);
        List<Person> persons = personDtos.stream().map(personMapper::toEntity).collect(Collectors.toList());
        personRepository.saveAll(persons);
        return persons.size() + " persons have been imported successfully.";
    }
    public byte[] exportPersonsToExcel() throws IOException {
        List<PersonDto> personDtos = personRepository.findAll().stream().map(personMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(personDtos, PersonDto.class);
    }

    public byte[] generatePersonTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(PersonDto.class);
    }
    protected String convertDateToJalali(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        DateConverter dateConverter = new DateConverter();
        int gregorianYear = localDate.getYear();
        int gregorianMonth = localDate.getMonthValue();
        int gregorianDay = localDate.getDayOfMonth();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(gregorianYear, gregorianMonth, gregorianDay);

        if (jalaliDate != null) {
            return jalaliDate.format(new JalaliDateFormatter("yyyy/mm/dd"));
        }
        return null;
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
        PersonDto dto = personMapper.toDto(findPersonById(personId));
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
    }

    public PersonDto createPerson(PersonDto personDto){
        Optional<Person> optionalPerson = personRepository.findPersonByNationalId(personDto.getNationalId());
        if (optionalPerson.isPresent()) throw new DuplicateNationalIdException("کد ملی تکراری است.");
        Person entity = personMapper.toEntity(personDto);
        Person saved = personRepository.save(entity);
        return personMapper.toDto(saved);
    }

    public PersonDto updatePerson(Long personId, PersonDto personDto) {
        Person person = findPersonById(personId);
        person.setFirstName( personDto.getFirstName() );
        person.setLastName( personDto.getLastName() );
        person.setFatherName( personDto.getFatherName() );
        person.setNationalId( personDto.getNationalId() );
        person.setBirthDate( personDto.getBirthDate() );
        person.setRegistrationNumber( personDto.getRegistrationNumber() );
        person.setPostalCode( personDto.getPostalCode() );
        person.setAddress( personDto.getAddress() );
        person.setPhoneNumber( personDto.getPhoneNumber() );
        Person updated = personRepository.save(person);
        return personMapper.toDto(updated);
    }

    public String removePerson(Long personId){
        boolean result = personRepository.existsById(personId);
        if (result){
            if (documentRepository.existsByPersonId(personId)) {
                throw new DatabaseIntegrityViolationException("امکان حذف فرد وجود ندارا. ابتدا همه سندهای این فرد را حذف کنید.");
            }
            if (boardMemberRepository.existsByPersonId(personId)){
                throw new DatabaseIntegrityViolationException("امکان حذف فرد وجود ندارا. ابتدا همه سمت های این فرد را حذف کنید.");
            }
            if (shareholderRepository.existsByPersonId(personId)){
                throw new DatabaseIntegrityViolationException("امکان حذف فرد وجود ندارا. ابتدا همه سهام های این فرد را حذف کنید.");
            }
            personRepository.deleteById(personId);
            return "فرستنده با موفقیت حذف شد. ";
        }
        return "خطا در حذف فرستنده.";
    }
}
