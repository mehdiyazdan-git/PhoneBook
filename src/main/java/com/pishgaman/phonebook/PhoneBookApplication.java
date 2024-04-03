package com.pishgaman.phonebook;

import com.pishgaman.phonebook.entities.Person;
import com.pishgaman.phonebook.entities.Product;
import com.pishgaman.phonebook.repositories.PersonRepository;
import com.pishgaman.phonebook.repositories.ProductRepository;
import com.pishgaman.phonebook.security.auth.AuthenticationService;
import com.pishgaman.phonebook.security.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.pishgaman.phonebook.security.user.Role.ADMIN;
import static com.pishgaman.phonebook.security.user.Role.MANAGER;

@SpringBootApplication
public class PhoneBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneBookApplication.class, args);
    }
    @Bean
    public CommandLineRunner loadData(PersonRepository personRepository, ProductRepository productRepository, AuthenticationService service) {
        return args -> {

            var admin = RegisterRequest.builder()
                    .firstname("مهدی")
                    .lastname("یزدان پرست")
                    .username("یزدان")
                    .email("yazdan@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();

            var manager = RegisterRequest.builder()
                    .firstname("ali")
                    .lastname("kasra")
                    .username("Manager")
                    .email("manager@mail.com")
                    .password("password")
                    .role(MANAGER)
                    .build();

            if (service.isTableEmpty()){
                System.out.println("Admin token: " + service.register(admin).getAccessToken());
                System.out.println("Manager token: " + service.register(manager).getAccessToken());
            }

            if (personRepository.count() == 0) {
                // Insert mock data for Persons
                personRepository.saveAll(Arrays.asList(
                        new Person(null, "John Doe", "Doe", "09129357731","domain@mail.com"),
                        new Person(null, "Smith","Jane Smith",  "09129357731","domain@mail.com"),
                        new Person(null, "Johnson","Bob Johnson",  "09129357731","domain@mail.com")
                ));
            }

            // Check if the Product repository is empty
            if (productRepository.count() == 0) {
                // Insert mock data for Products
                productRepository.saveAll(Arrays.asList(
                        new Product(null, "Laptop", "High-performance laptop for professionals.", new BigDecimal("999.00")),
                        new Product(null, "Smartphone", "Latest smartphone with top features.", new BigDecimal("699.00")),
                        new Product(null, "Headphones", "Noise-cancelling headphones with high-fidelity sound.", new BigDecimal("199.00"))
                ));
            }
        };
    }
}
