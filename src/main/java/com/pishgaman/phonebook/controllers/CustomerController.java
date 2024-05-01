package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.CustomerDto;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.searchforms.CustomerSearch;
import com.pishgaman.phonebook.services.CustomerService;
import com.pishgaman.phonebook.utils.FileMediaType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/download-all-customers.xlsx")
    public ResponseEntity<byte[]> downloadAllCustomersExcel() throws IOException {
        byte[] excelData = customerService.generateAllCustomersExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("all_customers.xlsx")
                .build());
        return ResponseEntity.ok().headers(headers).body(excelData);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importCustomersFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<CustomerDto> customerDtoList = customerService.importCustomersFromExcel(file);
            return ResponseEntity.status(HttpStatus.OK).body(customerDtoList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import from Excel file: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing Excel file: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadCustomerTemplate() {
        try {
            byte[] templateBytes = customerService.generateCustomerTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "customer_template.xlsx");
            headers.setContentType(FileMediaType.getMediaType("xlsx"));

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }


    @GetMapping(path = {"/",""})
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            CustomerSearch search) {
        Page<CustomerDto> customers = customerService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(customers);
    }
    @GetMapping(path = "/search")
    public ResponseEntity<List<CustomerDto>> searchCustomerByNameContaining(@RequestParam("searchQuery") String searchQuery) {
        List<CustomerDto> customers = customerService.searchCustomerByNameContaining(searchQuery);
        return ResponseEntity.ok(customers);
    }
    @GetMapping(path = "/select")
    public ResponseEntity<List<CustomerSelect>> findAllCustomerSelect(
            @RequestParam(required = false) String searchQuery) {
        List<CustomerSelect> customers = customerService.findAllCustomerSelect(searchQuery);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long customerId) {
        CustomerDto customer = customerService.findById(customerId);
        return ResponseEntity.ok(customer);
    }

    @PostMapping(path = {"/",""})
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDto customerDto) {
        try {
            CustomerDto createdCustomer = customerService.createCustomer(customerDto);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        }catch (EntityAlreadyExistsException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }catch (DateTimeParseException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Please use the format YYYY-MM-DD");
        }
    }
    @PutMapping(path = "/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable("customerId") Long customerId, @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
        }catch (EntityNotFoundException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (DateTimeParseException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Please use the format YYYY-MM-DD");
        }catch (EntityAlreadyExistsException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("customerId") Long customerId) {
        try {
            String message = customerService.removeCustomer(customerId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
