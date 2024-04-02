package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.CustomerDto;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.CustomerMapper;
import com.pishgaman.phonebook.repositories.CustomerRepository;
import com.pishgaman.phonebook.searchforms.CustomerSearch;
import com.pishgaman.phonebook.specifications.CustomerSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
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
        return customerMapper.toDto(findCustomerById(customerId));
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
        Customer customerById = findCustomerById(customerId);

        String newName = customerDto.getName();
        Customer customerByName = customerRepository.findCustomerByName(newName);

        if (customerByName != null && !customerByName.getId().equals(customerId)) {
            // Another customer with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای مشتری قبلاً ثبت شده است.");
        }

        Customer customerToBeUpdate = customerMapper.partialUpdate(customerDto, customerById);
        Customer updated = customerRepository.save(customerToBeUpdate);
        return customerMapper.toDto(updated);
    }

    public String removeCustomer(Long customerId) {
        boolean result = customerRepository.existsById(customerId);
        if (result){
            if (customerRepository.hasAssociatedLetter(customerId)) {
                throw new IllegalStateException("اشکال! این مشتری دارای نامه مرتبط می‌باشد و نمی‌تواند حذف شود.");
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

