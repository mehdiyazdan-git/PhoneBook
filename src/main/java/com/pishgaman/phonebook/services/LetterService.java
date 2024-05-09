package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.dtos.imports.LetterExcelDto;
import com.pishgaman.phonebook.dtos.imports.LetterExcelMapper;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.LetterType;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.mappers.LetterMapper;
import com.pishgaman.phonebook.repositories.*;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.utils.DateConvertor;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelTemplateGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;
    private final LetterMapper letterMapper;
    private final CustomerService customerService;
    private final YearRepository yearRepository;
    private final LetterSearchDao letterSearchDao;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final LetterTypeRepository letterTypeRepository;
    private final LetterExcelMapper letterExcelMapper;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }
    public LetterDto findById(Long letterId){
        Letter letter = letterRepository.findById(letterId).orElseThrow(() -> new EntityNotFoundException("نامه با شناسه " + letterId + " یافت نشد."));
        LetterDto dto = letterMapper.toDto(letter);
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;

    }

    public List<LetterDto> getAllLetters() {
        List<Letter> letters = letterRepository.findAll();
        return letters.stream().map(letterMapper::toDto).collect(Collectors.toList());
    }

    public Page<LetterDetailsDto> findAllLetterDetails(
            LetterSearch search,
            Integer page,
            Integer size,
            String sortBy,
            String order
    ) {
        return letterSearchDao.findAllBySimpleQuery(search,page,size,sortBy,order);
    }

    public LetterDto getLetterById(Long letterId) {
        Letter letter = findLetterById(letterId);
        LetterDto dto = letterMapper.toDto(letter);
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
    }
    private String reverseLetterNumber(String letterNumber) {
        String[] parts = letterNumber.split("/");
        for (int i = 0; i < parts.length / 2; i++) {
            String temp = parts[i];
            parts[i] = parts[parts.length - 1 - i];
            parts[parts.length - 1 - i] = temp;
        }
        return String.join("/", parts);
    }

    public String generateLetterNumber(Long companyId, Long yearId) {
        Year targetYear = yearRepository.findById(yearId).orElseThrow(() ->
                new IllegalArgumentException("Year not found for ID: " + yearId));
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new IllegalArgumentException("Company not found for ID: " + companyId));

        Map<Company, Integer> letterCounters = targetYear.getLetterCounters();
        if (!letterCounters.containsKey(company) || letterCounters.get(company) == 0) {

            letterCounters.put(company, Math.toIntExact(letterCounters.get(company) != null ? letterCounters.get(company): 0));
            int currentCount = letterCounters.get(company);
            return targetYear.getName() + "/" + company.getLetterPrefix() + "/" + (currentCount  + targetYear.getStartingLetterNumber().intValue());
        } else {

            letterCounters.put(company, (letterCounters.get(company)));
            int currentCount = letterCounters.get(company);
            return targetYear.getName() + "/" + company.getLetterPrefix() + "/" + (currentCount  + targetYear.getStartingLetterNumber().intValue());
        }
    }

    @Transactional
    public LetterDto createLetter(LetterDto letterDto) {
        try {
            Letter letter = new Letter();

            if (letterTypeRepository.findById(letterDto.getLetterTypeId()).isEmpty() ||
                !companyService.existById(letterDto.getCompanyId()) ||
                !customerService.existById(letterDto.getCustomerId()) ||
                !yearRepository.existsById(letterDto.getYearId())) {
                throw new IllegalArgumentException("Validation failed for one or more entities.");
            }

            letter.setLetterType(letterTypeRepository.findById(letterDto.getLetterTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Letter type not found")));
            letter.setCompany(companyRepository.findById(letterDto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found")));
            letter.setCustomer(customerRepository.findById(letterDto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found")));
            letter.setYear(yearRepository.findById(letterDto.getYearId())
                    .orElseThrow(() -> new EntityNotFoundException("Year not found")));

            letter.setContent(letterDto.getContent());
            letter.setCreationDate(letterDto.getCreationDate());
            letter.setLetterState(letterDto.getLetterState());

            if (letterDto.getLetterNumber() == null || letterDto.getLetterNumber().isEmpty() ||
                letterDto.getLetterNumber().isBlank() || letterDto.getLetterNumber().equals("undefined")) {
                String letterNumber = generateLetterNumber(letterDto.getCompanyId(), letterDto.getYearId());
                letter.setLetterNumber(reverseLetterNumber(letterNumber));
                Letter saved = letterRepository.save(letter);
                LetterType letterType = saved.getLetterType();
                if (letterType.getType().equals("OUTGOING")) {
                    Year year = saved.getYear();
                    int currentCount = year.getLetterCounters().getOrDefault(saved.getCompany(), 0) + 1;
                    year.getLetterCounters().put(saved.getCompany(), currentCount);
                    yearRepository.save(year);
                }
                return letterMapper.toDto(saved);
            }
            else {
                letter.setLetterNumber(letterDto.getLetterNumber());
                Letter saved = letterRepository.save(letter);
                LetterType letterType = saved.getLetterType();
                if (letterType.getType().equals("OUTGOING")) {
                    Year year = saved.getYear();
                    int currentCount = year.getLetterCounters().getOrDefault(saved.getCompany(), 0) + 1;
                    year.getLetterCounters().put(saved.getCompany(), currentCount);
                    yearRepository.save(year);
                }
                return letterMapper.toDto(saved);
            }

        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            throw new ServiceException("Failed to create letter: " + ex.getMessage(), ex);
        } catch (DataAccessException ex) {
            throw new ServiceException("Database error occurred while creating letter: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ServiceException("An unexpected error occurred while creating letter: " + ex.getMessage(), ex);
        }
    }
    @Transactional
    public LetterDto updateLetter(Long letterId, LetterDto letterDto) {
        // Consolidate existence checks for better efficiency and error reporting
        if (!letterRepository.existsById(letterId) ||
                !companyService.existById(letterDto.getCompanyId()) ||
                !customerService.existById(letterDto.getCustomerId()) ||
                !yearRepository.existsById(letterDto.getYearId())) {
            throw new IllegalArgumentException("Validation failed for one or more entities.");
        }

        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("Letter with ID " + letterId + " not found."));

        letter.setLetterType(letterTypeRepository.findById(letterDto.getLetterTypeId()).orElse(null));
        letter.setCompany(companyRepository.findById(letterDto.getCompanyId()).orElse(null));
        letter.setCustomer(customerRepository.findById(letterDto.getCustomerId()).orElse(null));
        letter.setYear(yearRepository.findById(letterDto.getYearId()).orElse(null));

        letter.setContent(letterDto.getContent());
        letter.setCreationDate(letterDto.getCreationDate());
        letter.setLetterNumber(letterDto.getLetterNumber());
        letter.setLetterState(letterDto.getLetterState());

        return letterMapper.toDto(letterRepository.save(letter));
    }


    @Transactional
    public void updateLetterState(Long letterId, LetterState letterState) {

        letterRepository.updateLetterState(letterState, letterId);

        if (letterState.toString().equals("DRAFT")){
            letterRepository.updateDeletable(true,letterId);
        }
        if (letterState.toString().equals("DELIVERED")){
            letterRepository.updateDeletable(false,letterId);
        }

    }


    @Transactional
    public String deleteLetter(Long letterId) {
        try {
            Letter letter = letterRepository.findById(letterId)
                    .orElseThrow(() -> new IllegalStateException("Letter with ID " + letterId + " not found."));
            if (isLastLetter(letter)) {
                decrementLetterCounter(letter);
            }
            letterRepository.deleteById(letterId);
            return "Letter with ID " + letterId + " successfully deleted.";
        } catch (Exception e) {
            throw new RuntimeException("Error deleting letter: " + e.getMessage(), e);
        }
    }

    private boolean isLastLetter(Letter letter) {
        Year year = letter.getYear();
        Company company = letter.getCompany();
        String[] parts = letter.getLetterNumber().split("/");
        int letterCounter = Integer.parseInt(parts[0]);
        int currentYearCounter = year.getLetterCounters().getOrDefault(company, 0);
        System.out.println(
                "currentYearCounter : "
                + currentYearCounter+ " letterCounter : "
                + letterCounter + " StartingLetterNumber : "
                + year.getStartingLetterNumber());
        System.out.println(currentYearCounter - 1 + year.getStartingLetterNumber() == letterCounter);
        return currentYearCounter - 1 + year.getStartingLetterNumber() == letterCounter;
    }

    private void decrementLetterCounter(Letter letter) {
        Company company = letter.getCompany();
        Year year = letter.getYear();
        int currentCount = year.getLetterCounters().getOrDefault(company, 1);
        currentCount = Math.max(0, currentCount - 1);
        year.getLetterCounters().put(company, currentCount);
        yearRepository.save(year);
    }


    private Letter findLetterById(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("نامه با شناسه : " + letterId + " یافت نشد."));
    }

    public byte[] exportLettersToExcel() throws IOException {
        List<LetterDto> letterDtoList = letterRepository
                .findAll()
                .stream()
                .map(letterMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(letterDtoList, LetterDto.class);
    }


    public byte[] generateLetterTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(LetterDto.class);
    }

    @Transactional
    public String importLettersFromExcel(MultipartFile file) throws IOException {
        List<LetterExcelDto> letterExcelDtos = readLettersFromExcel(file);
        List<Letter> letters = letterExcelDtos.stream()
                .map(letterExcelMapper::toEntity)
                .collect(Collectors.toList());
        letterRepository.saveAll(letters);
        return letters.size() + " letters have been imported successfully.";
    }

    private List<LetterExcelDto> readLettersFromExcel(MultipartFile file) throws IOException {
        List<LetterExcelDto> letterExcelDtos = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        // Assuming specific columns for simplicity
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            LetterExcelDto dto = new LetterExcelDto(
                    getLongCellValue(row.getCell(0)),      // id
                    row.getCell(1).getStringCellValue(),   // content
                    row.getCell(2).getStringCellValue(),   // creationDate
                    getLongCellValue(row.getCell(3)),      // letterTypeId
                    row.getCell(4).getStringCellValue(),   // letterNumber
                    (int)row.getCell(5).getNumericCellValue(), // customerCreatedBy
                    getLongCellValue(row.getCell(6)),      // companyId
                    getLongCellValue(row.getCell(7)),      // yearId
                    LetterState.valueOf(row.getCell(8).getStringCellValue())  // letterState
            );
            letterExcelDtos.add(dto);
        }
        workbook.close();
        return letterExcelDtos;
    }

    private Long getLongCellValue(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        return null;  // or handle String to Long conversion or throw
    }
}
