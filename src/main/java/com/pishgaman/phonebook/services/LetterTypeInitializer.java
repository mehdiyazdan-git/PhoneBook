package com.pishgaman.phonebook.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pishgaman.phonebook.entities.LetterType;
import com.pishgaman.phonebook.repositories.LetterTypeRepository;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.utils.DateConvertor;
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
public class LetterTypeInitializer {
    private final LetterTypeRepository letterTypeRepository;



    @PostConstruct
    public void init() throws IOException {
        // Check if the table is empty
        if (letterTypeRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("LETTER_TYPE.JSON");
            String jsonData = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            ObjectMapper mapper = new ObjectMapper();
            List<LetterType> letterTypes = mapper.readValue(jsonData, new TypeReference<>() {});
//            letterTypeRepository.saveAll(letterTypes);
        }
    }
}
