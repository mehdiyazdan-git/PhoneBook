package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.InsuranceSlipDto;
import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.dtos.imports.LetterExcelDto;
import com.pishgaman.phonebook.dtos.imports.LetterExcelMapper;
import com.pishgaman.phonebook.entities.*;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.mappers.LetterMapper;
import com.pishgaman.phonebook.repositories.*;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.utils.DateConvertor;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelDataImporter;
import com.pishgaman.phonebook.utils.ExcelTemplateGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public LetterDto createLetter(LetterDto letterDto) {
        Letter letter = new Letter();

        if (letterTypeRepository.findById(letterDto.getLetterTypeId()).isEmpty() ||
                !companyService.existById(letterDto.getCompanyId()) ||
                !customerService.existById(letterDto.getCustomerId()) ||
                !yearRepository.existsById(letterDto.getYearId())) {
            throw new IllegalArgumentException("Validation failed for one or more entities.");
        }
        if (letterDto.getLetterNumber() == null) {
            letter.setLetterNumber(generateLetterNumber(letterDto.getCompanyId(), letterDto.getYearId()));
        }

        letter.setLetterType(letterTypeRepository.findById(letterDto.getLetterTypeId()).orElse(null));
        letter.setCompany(companyRepository.findById(letterDto.getCompanyId()).orElse(null));
        letter.setCustomer(customerRepository.findById(letterDto.getCustomerId()).orElse(null));
        letter.setYear(yearRepository.findById(letterDto.getYearId()).orElse(null));

        letter.setContent(letterDto.getContent());
        letter.setCreationDate(letterDto.getCreationDate());
        letter.setLetterNumber(letterDto.getLetterNumber());
        letter.setLetterState(letterDto.getLetterState());

        Letter saved = letterRepository.save(letter);

        LetterType letterType = letterTypeRepository.findById(letterDto.getLetterTypeId()).orElse(null);
        if (letterType.getType().equals("OUTGOING")){
            int maxLetterCount = companyService.getLetterCounterById(letterDto.getCompanyId());
            companyService.incrementLetterCountByOne(maxLetterCount + 1, letterDto.getCompanyId());
        }


        return letterMapper.toDto(saved);
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

        // Reduce findById calls by setting directly if present
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
            Letter letterById = findLetterById(letterId);
            if (letterById == null) {
                throw new IllegalStateException("نامه با شناسه " + letterId + " یافت نشد.");
            }
            letterRepository.deleteById(letterId);
            Company company = letterById.getCompany();
            company.setLetterCounter(company.getLetterCounter() - 1);
            companyRepository.save(company);
            return "نامه با شناسه " + letterId + " با موفقیت حذف شد.";
        } catch (Exception e) {
            throw new RuntimeException("خطا در حذف نامه : " + e.getMessage(), e);
        }
    }

    private Letter findLetterById(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("نامه با شناسه : " + letterId + " یافت نشد."));
    }

    public String generateLetterNumber(Long companyId,Long yearName) {
        Year year = getYear(yearName);
        Long startingLetterNumber = year.getStartingLetterNumber();

        long count = companyService.getLetterCounterById(companyId) + 1 + startingLetterNumber;
        String letterPrefix = companyService.getLetterPrefixById(companyId);
        return yearName + "/" + letterPrefix + "/" + count;
    }
    protected Year getYear(Long yearName) {
        Optional<Year> optionalYear = yearRepository.findByYearName(yearName);
        return optionalYear.orElseThrow(() -> new EntityNotFoundException("سال با مقدار " + yearName + " یافت نشد."));
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
