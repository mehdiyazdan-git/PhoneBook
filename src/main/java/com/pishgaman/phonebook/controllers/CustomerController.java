package com.pishgaman.phonebook.controllers;

import com.pishgaman.phonebook.dtos.CustomerDto;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.searchforms.CustomerSearch;
import com.pishgaman.phonebook.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable("customerId") Long customerId, @RequestBody CustomerDto customerDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
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
