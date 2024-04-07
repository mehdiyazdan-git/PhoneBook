package com.pishgaman.phonebook.services;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.github.eloyzone.jalalicalendar.JalaliDateFormatter;
import com.pishgaman.phonebook.dtos.CompanyDto;
import com.pishgaman.phonebook.dtos.CompanySelect;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.CompanyMapper;
import com.pishgaman.phonebook.repositories.*;
import com.pishgaman.phonebook.searchforms.CompanySearch;
import com.pishgaman.phonebook.specifications.CompanySpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final DocumentRepository documentRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ShareholderRepository shareholderRepository;
    private final LetterRepository letterRepository;


    public byte[] generateAllCompaniesExcel() throws IOException {
        try {
            List<CompanyDto> allCompanies = companyRepository.findAll()
                    .stream().map(companyMapper::toDto).toList();
            if (allCompanies.isEmpty()) {
                throw new EntityNotFoundException("هیچ شرکتی یافت نشد.");
            }

            // Create a new workbook
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Companies");
            sheet.setRightToLeft(true);


            int rowIndex = 0;
            XSSFRow headerRow = sheet.createRow(rowIndex++);
            XSSFCellStyle headerStyle = createCellStyle(workbook);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            headerRow.createCell(0).setCellValue("شناسه");
            headerRow.createCell(1).setCellValue("کد اقتصادی");
            headerRow.createCell(2).setCellValue("شماره پرونده مالیاتی");
            headerRow.createCell(3).setCellValue("طبقه پرونده مالیاتی");
            headerRow.createCell(4).setCellValue("شناسه رهگیری مالیاتی");
            headerRow.createCell(5).setCellValue("نام کاربری درگاه مالیات");
            headerRow.createCell(6).setCellValue("رمز عبور درگاه مالیات");
            headerRow.createCell(7).setCellValue("حوزه مالیاتی");
            headerRow.createCell(8).setCellValue("نام شرکت");
            headerRow.createCell(9).setCellValue("کد ملی");
            headerRow.createCell(10).setCellValue("شماره ثبت");
            headerRow.createCell(11).setCellValue("تاریخ ثبت"); // Assuming registrationDate
            headerRow.createCell(12).setCellValue("آدرس");
            headerRow.createCell(13).setCellValue("کد پستی");
            headerRow.createCell(14).setCellValue("شماره تلفن");
            headerRow.createCell(15).setCellValue("شماره فکس");
            headerRow.createCell(16).setCellValue("نام کاربری نرم افزار");
            headerRow.createCell(17).setCellValue("رمز عبور نرم افزار");

            headerRow.forEach(cell -> cell.setCellStyle(headerStyle));

            XSSFCellStyle dataStyle = createCellStyle(workbook);


            for (CompanyDto company : allCompanies) {
                XSSFRow dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(company.getId());
                dataRow.createCell(1).setCellValue(company.getTaxEconomicCode());
                dataRow.createCell(2).setCellValue(company.getTaxFileNumber());
                dataRow.createCell(3).setCellValue(company.getTaxFileClass());
                dataRow.createCell(4).setCellValue(company.getTaxTrackingID());
                dataRow.createCell(5).setCellValue(company.getTaxPortalUsername());
                dataRow.createCell(6).setCellValue(company.getTaxPortalPassword()); // Consider security implications
                dataRow.createCell(7).setCellValue(company.getTaxDepartment());
                dataRow.createCell(8).setCellValue(company.getCompanyName());
                dataRow.createCell(9).setCellValue(company.getNationalId());
                dataRow.createCell(10).setCellValue(company.getRegistrationNumber());
                dataRow.createCell(11).setCellValue(convertDateToJalali(company.getRegistrationDate())); // Assuming desired format
                dataRow.createCell(12).setCellValue(company.getAddress());
                dataRow.createCell(13).setCellValue(company.getPostalCode());
                dataRow.createCell(14).setCellValue(company.getPhoneNumber());
                dataRow.createCell(15).setCellValue(company.getFaxNumber());
                dataRow.createCell(16).setCellValue(company.getSoftwareUsername());
                dataRow.createCell(17).setCellValue(company.getSoftwarePassword()); // Consider security implications

                dataRow.forEach(cell -> cell.setCellStyle(dataStyle));
            }
            // Autosize columns
            for (int i = 0; i < 17; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error generating Excel file: " + e.getMessage());
        }
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

        XSSFFont font = workbook.createFont();
        font.setFontName("B Nazanin");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        return style;
    }

    private XSSFCellStyle createBorderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());

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


    public List<CompanyDto> readCompaniesFromExcel(MultipartFile file) throws IOException {
        List<CompanyDto> companyDtoList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }
                CompanyDto company = new CompanyDto();
                company.setId(Long.parseLong(row.getCell(0).getStringCellValue()));
                company.setTaxEconomicCode(row.getCell(1).getStringCellValue());
                company.setTaxFileNumber(row.getCell(2).getStringCellValue());
                company.setTaxFileClass(row.getCell(3).getStringCellValue());
                company.setTaxTrackingID(row.getCell(4).getStringCellValue());
                company.setTaxPortalUsername(row.getCell(5).getStringCellValue());
                company.setTaxPortalPassword(row.getCell(6).getStringCellValue());
                company.setTaxDepartment(row.getCell(7).getStringCellValue());
                company.setCompanyName(row.getCell(8).getStringCellValue());
                company.setNationalId(row.getCell(9).getStringCellValue());
                company.setRegistrationNumber(row.getCell(10).getStringCellValue());
                company.setRegistrationDate(convertJalaliToGregorian(row.getCell(11).getStringCellValue()));
                company.setAddress(row.getCell(12).getStringCellValue());
                company.setPostalCode(row.getCell(13).getStringCellValue());
                company.setPhoneNumber(row.getCell(14).getStringCellValue());
                company.setFaxNumber(row.getCell(15).getStringCellValue());
                company.setSoftwareUsername(row.getCell(16).getStringCellValue());
                company.setSoftwarePassword(row.getCell(17).getStringCellValue());
                companyDtoList.add(company);
            }
        }
        return companyDtoList;

    }

    public void saveCompanies(List<CompanyDto> companyDtoList) {
        List<Company> companyList = new ArrayList<>();
        for (CompanyDto companyDto : companyDtoList) {
            Company company = companyMapper.toEntity(companyDto);
            companyList.add(company);
        }
        companyRepository.saveAll(companyList);
    }

    public LocalDate convertJalaliToGregorian(String jalaliDate) {
        DateConverter dateConverter = new DateConverter();

        String[] parts = jalaliDate.split("/");
        int jalaliYear = Integer.parseInt(parts[0]);
        int jalaliMonth = Integer.parseInt(parts[1]);
        int jalaliDay = Integer.parseInt(parts[2]);
        JalaliDate jalaliDate1 = dateConverter.gregorianToJalali(jalaliYear, jalaliMonth, jalaliDay);

        if (jalaliDate1 != null) {
            return LocalDate.of(jalaliDate1.getYear(), jalaliDate1.getMonthPersian().getValue(), jalaliDate1.getDay());
        }
        return null;
    }

    ;

    public Page<CompanyDto> findAll(CompanySearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Company> specification = CompanySpecification.getSpecification(search);
        return companyRepository.findAll(specification, pageRequest)
                .map(companyMapper::toDto);
    }

    public List<CompanySelect> findAllCompanySelect(String searchParam) {
        Specification<Company> specification = CompanySpecification.getSelectSpecification(searchParam);
        return companyRepository.findAll(specification).stream().map(companyMapper::toSelectDto).collect(Collectors.toList());
    }

    public List<CompanySelect> searchCompanyByNameContaining(String searchQuery) {
        return companyRepository.findByCompanyNameContains(searchQuery).stream().map(companyMapper::toSelectDto).collect(Collectors.toList());
    }

    public int getLetterCounterById(Long companyId) {
        return companyRepository.getMaxLetterCountByCompanyId(companyId);
    }

    private Company findCompanyById(Long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isEmpty()) {
            throw new EntityNotFoundException("شرکت با شناسه : " + companyId + " یافت نشد.");
        }
        return optionalCompany.get();
    }

    public CompanyDto findById(Long companyId) {
        return companyMapper.toDto(findCompanyById(companyId));
    }

    public CompanyDto createCompany(CompanyDto companyDto) {
        Company companyByName = companyRepository.findCompanyByCompanyName(companyDto.getCompanyName());
        if (companyByName != null) {
            throw new EntityAlreadyExistsException("اشکال! شرکت با نام '" + companyDto.getCompanyName() + "' قبلاً ثبت شده است.");
        }
        Company entity = companyMapper.toEntity(companyDto);
        Company saved = companyRepository.save(entity);
        return companyMapper.toDto(saved);
    }

    public CompanyDto updateCompany(Long companyId, CompanyDto companyDto) {
        Company companyById = findCompanyById(companyId);

        // Check if the new name is unique
        String newName = companyDto.getCompanyName();
        Company companyByName = companyRepository.findCompanyByCompanyName(newName);

        if (companyByName != null && !companyByName.getId().equals(companyId)) {
            // Another company with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای شرکت قبلاً ثبت شده است.");
        }

        Company companyToBeUpdate = companyMapper.partialUpdate(companyDto, companyById);
        Company updated = companyRepository.save(companyToBeUpdate);
        return companyMapper.toDto(updated);
    }

    public void incrementLetterCountByOne(Integer count, Long senderId) {
        companyRepository.incrementLetterCountByOne(count, senderId);
    }

    public String getLetterPrefixById(Long companyId) {
        Company company = findCompanyById(companyId);
        return company.getLetterPrefix();
    }

    public String removeCompany(Long companyId) {
        boolean result = companyRepository.existsById(companyId);
        if (result) {
            if (documentRepository.existsByPersonId(companyId)) {
                throw new RuntimeException("امکان حذف شرکت وجود ندارا. ابتدا همه سندهای این شرکت را حذف کنید.");
            }
            if (boardMemberRepository.existsByPersonId(companyId)){
                throw new RuntimeException("امکان حذف شرکت وجود ندارا. ابتدا همه سمت های این شرکت را حذف کنید.");
            }
            if (shareholderRepository.existsByPersonId(companyId)){
                throw new RuntimeException("امکان حذف شرکت وجود ندارا. ابتدا همه سهامدارای این شرکت را حذف کنید.");
            }
            if (letterRepository.existsByCompanyId(companyId)){
                throw new RuntimeException("امکان حذف شرکت وجود ندارا. ابتدا همه نامه های این شرکت را حذف کنید.");
            }
            companyRepository.deleteById(companyId);
            return "شرکت با موفقیت حذف شد.";
        }

        return "خطا در حذف شرکت.";
    }

    public boolean existById(Long id) {
        return companyRepository.existsById(id);
    }
}
