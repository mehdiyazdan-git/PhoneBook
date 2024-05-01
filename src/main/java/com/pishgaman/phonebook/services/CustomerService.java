package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.CustomerDto;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.dtos.DocumentDto;
import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.exceptions.DatabaseIntegrityViolationException;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.CustomerMapper;
import com.pishgaman.phonebook.repositories.CustomerRepository;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.searchforms.CustomerSearch;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.CustomerSpecification;
import com.pishgaman.phonebook.utils.*;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pishgaman.phonebook.utils.DateConvertor.convertJalaliToGregorian;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

    public List<CustomerDto> importCustomersFromExcel(MultipartFile file) throws IOException {
        List<CustomerDto> customers = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        // Skip the header row
        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            CustomerDto customer = new CustomerDto();

            customer.setName(currentRow.getCell(0).getStringCellValue());
            customer.setAddress(currentRow.getCell(1).getStringCellValue());
            customer.setPhoneNumber(currentRow.getCell(2).getStringCellValue());
            customer.setNationalIdentity(currentRow.getCell(3).getStringCellValue());
            customer.setRegisterCode(currentRow.getCell(4).getStringCellValue());
            if (currentRow.getCell(5) != null && currentRow.getCell(5).getCellType() == CellType.STRING) {
                customer.setRegisterDate(convertJalaliToGregorian(currentRow.getCell(5).getStringCellValue()));
            }

            customers.add(customer);
        }

        workbook.close();
        List<Customer> customerList = customerRepository.saveAll(customers.stream().map(customerMapper::toEntity).collect(Collectors.toList()));
        return customerList.stream().map(customerMapper::toDto).collect(Collectors.toList());

    }

    public byte[] generateCustomerTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(CustomerDto.class);
    }


    public byte[] generateAllCustomersExcel() throws IOException {
        try {
            List<CustomerDto> allCustomers = customerRepository.findAll()
                    .stream()
                    .map(customerMapper::toDto)
                    .toList();
            if (allCustomers.isEmpty()) {
                throw new EntityNotFoundException("هیچ مشتری یافت نشد.");
            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Customers");
            sheet.setRightToLeft(true);

            int rowIndex = 0;
            XSSFRow headerRow = sheet.createRow(rowIndex++);
            XSSFCellStyle headerStyle = createCellStyle(workbook);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int i = 0; i < 5; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellStyle(headerStyle);
            }
            headerRow.getCell(0).setCellValue("شناسه");
            headerRow.getCell(1).setCellValue("نام");
            headerRow.getCell(2).setCellValue("آدرس");
            headerRow.getCell(3).setCellValue("شماره تلفن");
            headerRow.getCell(4).setCellValue("کد ملی");

            XSSFCellStyle dataStyle = createCellStyle(workbook);

            for (CustomerDto customer : allCustomers) {
                XSSFRow dataRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < 5; i++) {
                    XSSFCell cell = dataRow.createCell(i);
                    cell.setCellStyle(dataStyle);
                }
                dataRow.getCell(0).setCellValue(customer.getId());
                dataRow.getCell(1).setCellValue(customer.getName());
                dataRow.getCell(2).setCellValue(customer.getAddress());
                dataRow.getCell(3).setCellValue(customer.getPhoneNumber());
                dataRow.getCell(4).setCellValue(customer.getNationalIdentity());
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
//    public String importCustomersFromExcel(MultipartFile file) throws IOException {
//        List<CustomerDto> customerDtos = ExcelDataImporter.importData(file, CustomerDto.class);
//        List<Customer> customers = customerDtos.stream().map(customerMapper::toEntity).collect(Collectors.toList());
//        customerRepository.saveAll(customers);
//        return customers.size() + " customers have been imported successfully.";
//    }

    public byte[] exportCustomersToExcel() throws IOException {
        List<CustomerDto> customerDtos = customerRepository.findAll().stream().map(customerMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(customerDtos, CustomerDto.class);
    }


    public Page<CustomerDto> findAll(CustomerSearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Customer> specification = CustomerSpecification.getSpecification(search);
        return customerRepository.findAll(specification, pageRequest)
                .map(customerMapper::toDto);
    }
    public List<CustomerDto> searchCustomerByNameContaining(String searchQuery) {
        return customerRepository
                .findCustomerByNameContains(searchQuery)
                .stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<CustomerSelect> findAllCustomerSelect(String searchParam) {
        Specification<Customer> specification = CustomerSpecification.getSelectSpecification(searchParam);
        return customerRepository
                .findAll(specification)
                .stream()
                .map(customerMapper::toSelectDto)
                .collect(Collectors.toList());
    }

    private Customer findCustomerById(Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new EntityNotFoundException("مشتری با شناسه : " + customerId + " یافت نشد.");
        }
        return optionalCustomer.get();
    }

    public CustomerDto findById(Long customerId) {
        CustomerDto dto = customerMapper.toDto(findCustomerById(customerId));
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
    }
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customerByName = customerRepository.findCustomerByName(customerDto.getName());
        if (customerByName != null) {
            throw new EntityAlreadyExistsException("اشکال! گیرنده با نام '" + customerDto.getName() + "' قبلاً ثبت شده است.");
        }
        Customer entity = customerMapper.toEntity(customerDto);
        Customer saved = customerRepository.save(entity);
        return customerMapper.toDto(saved);
    }

    public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isEmpty()) {
            throw new EntityNotFoundException("اشکال! مشتری با شناسه : " + customerId + " یافت نشد.");
        }
        Customer customerById = optionalCustomer.get();

        if (customerRepository.findCustomerByNameAndIdNot(customerDto.getName(),customerId) != null) {
            throw new EntityAlreadyExistsException("اشکال! نام '" + customerDto.getName() + "'  قبلاً ثبت شده است.");
        }

        Customer customerToBeUpdate = customerMapper.partialUpdate(customerDto, customerById);
        customerToBeUpdate.setRegisterDate(customerDto.getRegisterDate());
        Customer updated = customerRepository.save(customerToBeUpdate);
        System.out.println("Customer updated successfully!");
        return customerMapper.toDto(updated);
    }

    public String removeCustomer(Long customerId) {
        boolean result = customerRepository.existsById(customerId);
        if (result){
            if (customerRepository.hasAssociatedLetter(customerId)) {
                throw new DatabaseIntegrityViolationException("اشکال! این مشتری دارای نامه مرتبط می‌باشد و نمی‌تواند حذف شود.");
            }
            customerRepository.deleteById(customerId);
            return  "مشتری با موفقیت حذف شد." ;
        }

        return "خطا در حذف مشتری.";
    }
    public boolean existById(Long id){
        return customerRepository.existsById(id);
    }
}

