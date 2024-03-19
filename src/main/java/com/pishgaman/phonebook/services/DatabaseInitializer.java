package com.pishgaman.phonebook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pishgaman.phonebook.entities.*;
import com.pishgaman.phonebook.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;
    private final SenderRepository senderRepository;
    private final YearRepository yearRepository;
    private final PositionRepository positionRepository;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule

        if (customerRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("data.json");
            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            List<Customer> customers = mapper.readValue(jsonData, new TypeReference<>() {});
            customerRepository.saveAll(customers);
        }

        if (personRepository.count() == 0) {
            ClassPathResource personResource = new ClassPathResource("person.json");
            String personJsonData = new String(Files.readAllBytes(Paths.get(personResource.getURI())));
            List<Person> persons = mapper.readValue(personJsonData, new TypeReference<>() {});
            personRepository.saveAll(persons);
        }

        if (companyRepository.count() == 0) {
            ClassPathResource companyResource = new ClassPathResource("company.json");
            String companyJsonData = new String(Files.readAllBytes(Paths.get(companyResource.getURI())));
            List<Company> companies = mapper.readValue(companyJsonData, new TypeReference<>() {});
            companyRepository.saveAll(companies);
        }

        if (senderRepository.count() == 0) {
            ClassPathResource senderResource = new ClassPathResource("sender.json");
            String senderJsonData = new String(Files.readAllBytes(Paths.get(senderResource.getURI())));
            List<Sender> senders = mapper.readValue(senderJsonData, new TypeReference<>() {});
            senderRepository.saveAll(senders);
        }

        if (yearRepository.count() == 0) {
            ClassPathResource yearResource = new ClassPathResource("year.json");
            String yearJsonData = new String(Files.readAllBytes(Paths.get(yearResource.getURI())));
            List<Year> years = mapper.readValue(yearJsonData, new TypeReference<>() {});
            yearRepository.saveAll(years);
        }
        if (positionRepository.count() == 0) {
            ClassPathResource positionResource = new ClassPathResource("position.json");
            String positionJsonData = new String(Files.readAllBytes(Paths.get(positionResource.getURI())));
            List<Position> positions = mapper.readValue(positionJsonData, new TypeReference<>() {});
            positionRepository.saveAll(positions);
        }
    }
}
