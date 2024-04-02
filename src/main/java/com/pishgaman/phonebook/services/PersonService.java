package com.pishgaman.phonebook.services;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.PersonSelectDto;
import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.mappers.PersonMapper;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.searchforms.PersonSearch;
import com.pishgaman.phonebook.specifications.PersonSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Interval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public byte[] generateAllPersonsExcel() throws IOException {
        try {
            List<PersonDto> allPersons = personRepository.findAll().stream().map(personMapper::toDto).toList();
            if (allPersons.isEmpty()) {
                throw new EntityNotFoundException("هیچ فردی یافت نشد.");
            }

            // Create a new workbook
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Persons");
            sheet.setColumnWidth(0,255*17);
            sheet.setColumnWidth(1,255*17);
            sheet.setColumnWidth(2,255*17);
            sheet.setColumnWidth(3,255*17);
            sheet.setColumnWidth(4,255*17);
            sheet.setColumnWidth(5,255*17);
            sheet.setColumnWidth(6,255*17);
            sheet.setColumnWidth(7,255*17);
            sheet.setColumnWidth(8,255*64);
            sheet.setColumnWidth(9,255*17);
            sheet.setRightToLeft(true);

            // Create headers for the table
            int rowIndex = 0;
            XSSFRow headerRow = sheet.createRow(rowIndex++);
            XSSFCellStyle headerStyle = createCellStyle(workbook);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            headerRow.createCell(0).setCellValue("شناسه");
            headerRow.createCell(1).setCellValue("نام");
            headerRow.createCell(2).setCellValue("نام خانوادگی");
            headerRow.createCell(3).setCellValue("نام پدر");
            headerRow.createCell(4).setCellValue("کد ملی");
            headerRow.createCell(5).setCellValue("تاریخ تولد");
            headerRow.createCell(6).setCellValue("شماره ثبت");
            headerRow.createCell(7).setCellValue("کد پستی");
            headerRow.createCell(8).setCellValue("آدرس");
            headerRow.createCell(9).setCellValue("شماره تلفن");

            headerRow.forEach(cell -> cell.setCellStyle(headerStyle));

            XSSFCellStyle dataStyle = createCellStyle(workbook);

            // Populate data rows
            for (PersonDto person : allPersons) {
                XSSFRow dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(person.getId());
                dataRow.createCell(1).setCellValue(person.getFirstName());
                dataRow.createCell(2).setCellValue(person.getLastName());
                dataRow.createCell(3).setCellValue(person.getFatherName());
                dataRow.createCell(4).setCellValue(person.getNationalId());
                dataRow.createCell(5).setCellValue(convertDateToJalali(person.getBirthDate()));
                dataRow.createCell(6).setCellValue(person.getRegistrationNumber());
                dataRow.createCell(7).setCellValue(person.getPostalCode());
                dataRow.createCell(8).setCellValue(person.getAddress());
                dataRow.createCell(9).setCellValue(person.getPhoneNumber());

                dataRow.forEach(cell -> cell.setCellStyle(dataStyle));
            }


            // Create a byte array to hold the Excel file data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error generating Excel file: " + e.getMessage());
        }
    }

    private int calculateCharWidth(String value) {
        // Implement logic to calculate character width based on your font and locale
        // This is a simplified example assuming all characters have the same width (can be improved)
        return value.length();
    }

    private XSSFCellStyle createCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("B Nazanin");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        return style;
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
