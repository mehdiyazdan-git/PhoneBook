package com.pishgaman.phonebook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pishgaman.phonebook.dtos.InsuranceSlipDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.dtos.ShareholderDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.entities.*;
import com.pishgaman.phonebook.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;
    private final SenderRepository senderRepository;
    private final YearRepository yearRepository;
    private final PositionRepository positionRepository;
    private final LetterTypeRepository letterTypeRepository;
    private final LetterRepository letterRepository;
    private final InsuranceSlipRepository insuranceSlipRepository;
    private final TaxPaymentSlipRepository taxPaymentSlipRepository;
    private final ShareholderRepository shareholderRepository;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule

        if (customerRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("data.json");
            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            List<Customer> customers = mapper.readValue(jsonData, new TypeReference<>() {
            });
            customerRepository.saveAll(customers);
        }

        if (personRepository.count() == 0) {
            ClassPathResource personResource = new ClassPathResource("person.json");
            String personJsonData = new String(Files.readAllBytes(Paths.get(personResource.getURI())));
            List<Person> persons = mapper.readValue(personJsonData, new TypeReference<>() {
            });
            personRepository.saveAll(persons);
        }

        if (companyRepository.count() == 0) {
            ClassPathResource companyResource = new ClassPathResource("company.json");
            String companyJsonData = new String(Files.readAllBytes(Paths.get(companyResource.getURI())));
            List<Company> companies = mapper.readValue(companyJsonData, new TypeReference<>() {
            });
            companyRepository.saveAll(companies);
        }

        if (senderRepository.count() == 0) {
            ClassPathResource senderResource = new ClassPathResource("sender.json");
            String senderJsonData = new String(Files.readAllBytes(Paths.get(senderResource.getURI())));
            List<Sender> senders = mapper.readValue(senderJsonData, new TypeReference<>() {
            });
            senderRepository.saveAll(senders);
        }

        if (yearRepository.count() == 0) {
            ClassPathResource yearResource = new ClassPathResource("year.json");
            String yearJsonData = new String(Files.readAllBytes(Paths.get(yearResource.getURI())));
            List<Year> years = mapper.readValue(yearJsonData, new TypeReference<>() {
            });
            yearRepository.saveAll(years);
        }
        if (positionRepository.count() == 0) {
            ClassPathResource positionResource = new ClassPathResource("position.json");
            String positionJsonData = new String(Files.readAllBytes(Paths.get(positionResource.getURI())));
            List<Position> positions = mapper.readValue(positionJsonData, new TypeReference<>() {
            });
            positionRepository.saveAll(positions);
        }
        if (letterTypeRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("LETTER_TYPE.JSON");
            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            List<LetterType> letterTypes = mapper.readValue(jsonData, new TypeReference<>() {
            });
            letterTypeRepository.saveAll(letterTypes);
        }
        if (letterRepository.count() == 0) {
            ClassPathResource letterResource = new ClassPathResource("letter.json");
            String letterJsonData = new String(Files.readAllBytes(Paths.get(letterResource.getURI())));
            List<Map<String, Object>> letterMaps = mapper.readValue(letterJsonData, new TypeReference<>() {
            });

            List<LetterDto> letterDtos = new ArrayList<>();
            for (Map<String, Object> letterMap : letterMaps) {
                LetterDto letterDto = mapper.convertValue(letterMap, LetterDto.class);
                letterDtos.add(letterDto);
            }
            for (LetterDto letterDto : letterDtos) {
                Letter letter = new Letter();
                letter.setLetterType(letterTypeRepository.findById(letterDto.getLetterTypeId()).orElse(null));
                letter.setCompany(companyRepository.findById(letterDto.getCompanyId()).orElse(null));
                letter.setCustomer(customerRepository.findById(letterDto.getCustomerId()).orElse(null));
                letter.setYear(yearRepository.findById(letterDto.getYearId()).orElse(null));
                letter.setId(letterDto.getId());
                letter.setContent(letterDto.getContent());
                letter.setCreationDate(letterDto.getCreationDate());
                letter.setLetterNumber(letterDto.getLetterNumber());
                letter.setLetterState(letterDto.getLetterState());
                letterRepository.save(letter);
            }
        }
        if (insuranceSlipRepository.count() == 0) {
            ClassPathResource insuranceSlipResource = new ClassPathResource("insurance_slip.json");

            String insuranceJsonData = new String(Files.readAllBytes(Paths.get(insuranceSlipResource.getURI())));
            List<Map<String, Object>> insuranceSlipMaps = mapper.readValue(insuranceJsonData, new TypeReference<>() {
            });

            List<InsuranceSlipDto> insuranceSlipDtoList = new ArrayList<>();
            for (Map<String, Object> insuranceMap : insuranceSlipMaps) {
                InsuranceSlipDto insuranceSlipDto = mapper.convertValue(insuranceMap, InsuranceSlipDto.class);
                insuranceSlipDtoList.add(insuranceSlipDto);
            }
            for (InsuranceSlipDto slipDto : insuranceSlipDtoList) {
                InsuranceSlip insuranceSlip = new InsuranceSlip();
                insuranceSlip.setCompany(companyRepository.findById(slipDto.getCompanyId()).orElse(null));
                insuranceSlip.setId(slipDto.getId());
                insuranceSlip.setAmount(slipDto.getAmount());
                insuranceSlip.setType(slipDto.getType());
                insuranceSlip.setIssueDate(slipDto.getIssueDate());
                insuranceSlip.setStartDate(slipDto.getStartDate());
                insuranceSlip.setEndDate(slipDto.getEndDate());
                insuranceSlip.setSlipNumber(slipDto.getSlipNumber());
                insuranceSlipRepository.save(insuranceSlip);
            }
        }
        if (taxPaymentSlipRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("tax_payment_slip.json");

            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            List<Map<String, Object>> taxPaymentSlipMaps = mapper.readValue(jsonData, new TypeReference<>() {
            });

            List<TaxPaymentSlipDto> taxPaymentSlipDtoList = new ArrayList<>();
            for (Map<String, Object> taxPaymentSlipMap : taxPaymentSlipMaps) {
                TaxPaymentSlipDto taxPaymentSlipDto = mapper.convertValue(taxPaymentSlipMap,TaxPaymentSlipDto.class);
                taxPaymentSlipDtoList.add(taxPaymentSlipDto);
            }
            for (TaxPaymentSlipDto taxPaymentSlipDto : taxPaymentSlipDtoList) {
                TaxPaymentSlip taxPaymentSlip = new TaxPaymentSlip();
                taxPaymentSlip.setCompany(companyRepository.findById(taxPaymentSlipDto.getCompanyId()).orElse(null));
                taxPaymentSlip.setId(taxPaymentSlipDto.getId());
                taxPaymentSlip.setAmount(taxPaymentSlipDto.getAmount());
                taxPaymentSlip.setType(taxPaymentSlipDto.getType());
                taxPaymentSlip.setIssueDate(taxPaymentSlipDto.getIssueDate());
                taxPaymentSlip.setAmount(taxPaymentSlipDto.getAmount());
                taxPaymentSlip.setPeriod(taxPaymentSlipDto.getPeriod());
                taxPaymentSlip.setSlipNumber(taxPaymentSlipDto.getSlipNumber());
                taxPaymentSlipRepository.save(taxPaymentSlip);
            }
        }
        if (shareholderRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("shareholder.json");

            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            List<Map<String, Object>> shareholderMaps = mapper.readValue(jsonData, new TypeReference<>() {
            });

            List<ShareholderDto> shareholderDtoList = new ArrayList<>();
            for (Map<String, Object> shareholderMap : shareholderMaps) {
                ShareholderDto shareholderDto = mapper.convertValue(shareholderMap, ShareholderDto.class);
                shareholderDtoList.add(shareholderDto);
            }
            for (ShareholderDto shareholderDto : shareholderDtoList) {
                Shareholder shareholder = new Shareholder();
                shareholder.setId(shareholderDto.getId());
                shareholder.setPerson(personRepository.findById(shareholderDto.getPersonId()).orElse(null));
                shareholder.setNumberOfShares(shareholderDto.getNumberOfShares());
                shareholder.setPercentageOwnership(shareholderDto.getPercentageOwnership());
                shareholder.setSharePrice(shareholderDto.getSharePrice());
                shareholder.setShareType(shareholderDto.getShareType());
                shareholder.setCompany(companyRepository.findById(shareholderDto.getCompanyId()).orElse(null));
                shareholder.setScannedShareCertificate(shareholderDto.getScannedShareCertificate());
                shareholder.setFileExtension(shareholderDto.getFileExtension());
                shareholderRepository.save(shareholder);
            }
        }

    }
}
