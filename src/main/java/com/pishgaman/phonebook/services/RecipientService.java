package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.SenderDto;
import com.pishgaman.phonebook.mappers.RecipientMapper;
import com.pishgaman.phonebook.dtos.RecipientDto;
import com.pishgaman.phonebook.entities.Recipient;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.RecipientMapper;
import com.pishgaman.phonebook.repositories.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipientService {
    private final RecipientRepository recipientRepository;
    private final RecipientMapper recipientMapper;

    @Autowired
    public RecipientService(RecipientRepository recipientRepository, RecipientMapper recipientMapper) {
        this.recipientRepository = recipientRepository;
        this.recipientMapper = recipientMapper;
    }

    public List<RecipientDto> findAll() {
        return recipientRepository.findAll().stream().map(recipientMapper::toDto).collect(Collectors.toList());
    }
    public List<RecipientDto> searchRecipientByNameContaining(String searchQuery) {
        return recipientRepository.findByRecipientNameContains(searchQuery).stream().map(recipientMapper::toDto).collect(Collectors.toList());
    }

    private Recipient findRecipientById(Long recipientId) {
        Optional<Recipient> optionalRecipient = recipientRepository.findById(recipientId);
        if (optionalRecipient.isEmpty()) {
            throw new EntityNotFoundException("گیرنده با شناسه : " + recipientId + " یافت نشد.");
        }
        return optionalRecipient.get();
    }

    public RecipientDto findById(Long recipientId) {
        return recipientMapper.toDto(findRecipientById(recipientId));
    }

    public RecipientDto createRecipient(RecipientDto recipientDto) {
        Recipient recipientByName = recipientRepository.findRecipientByName(recipientDto.getName());
        if (recipientByName != null) {
            throw new EntityAlreadyExistsException("اشکال! گیرنده با نام '" + recipientDto.getName() + "' قبلاً ثبت شده است.");
        }
        Recipient entity = recipientMapper.toEntity(recipientDto);
        Recipient saved = recipientRepository.save(entity);
        return recipientMapper.toDto(saved);
    }

    public RecipientDto updateRecipient(Long recipientId, RecipientDto recipientDto) {
        Recipient recipientById = findRecipientById(recipientId);

        // Check if the new name is unique
        String newName = recipientDto.getName();
        Recipient recipientByName = recipientRepository.findRecipientByName(newName);

        if (recipientByName != null && !recipientByName.getId().equals(recipientId)) {
            // Another recipient with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای گیرنده قبلاً ثبت شده است.");
        }

        Recipient recipientToBeUpdate = recipientMapper.partialUpdate(recipientDto, recipientById);
        Recipient updated = recipientRepository.save(recipientToBeUpdate);
        return recipientMapper.toDto(updated);
    }

    public String removeRecipient(Long recipientId) {
        boolean result = recipientRepository.existsById(recipientId);
        if (result){
            if (recipientRepository.hasAssociatedLetter(recipientId)) {
                throw new IllegalStateException("اشکال! این گیرنده دارای نامه مرتبط می‌باشد و نمی‌تواند حذف شود.");
            }
            recipientRepository.deleteById(recipientId);
            return  "گیرنده با موفقیت حذف شد." ;
        }

        return "خطا در حذف گیرنده.";
    }
    public boolean existById(Long id){
        return recipientRepository.existsById(id);
    }
}

