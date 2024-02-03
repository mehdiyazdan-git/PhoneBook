package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.SenderDto;
import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.SenderMapper;
import com.pishgaman.phonebook.repositories.SenderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SenderService {
    private final SenderRepository senderRepository;
    private final SenderMapper senderMapper;
    @Autowired
    public SenderService(SenderRepository senderRepository, SenderMapper senderMapper) {
        this.senderRepository = senderRepository;
        this.senderMapper = senderMapper;
    }
    public List<SenderDto> findAll(){
        return senderRepository.findAll().stream().map(senderMapper::toDto).collect(Collectors.toList());
    }
    public List<SenderDto> searchSenderByNameContaining(String searchQuery) {
        return senderRepository.findBySenderNameContains(searchQuery).stream().map(senderMapper::toDto).collect(Collectors.toList());
    }
    private Sender findSenderById(Long senderId){
        Optional<Sender> optionalSender = senderRepository.findById(senderId);
        if (optionalSender.isEmpty()) throw new EntityNotFoundException("فرستنده با شناسه : " + senderId + " یافت نشد.");
        return optionalSender.get();
    }
    public SenderDto findById(Long senderId){
        return senderMapper.toDto(findSenderById(senderId));
    }
    public SenderDto createSender(SenderDto senderDto){
        Sender senderByName = senderRepository.findSenderByName(senderDto.getName());
        if (senderByName != null) {
            throw new EntityAlreadyExistsException("اشکال! فرستنده با نام '" + senderDto.getName() + "' قبلاً ثبت شده است.");
        }
        Sender entity = senderMapper.toEntity(senderDto);
        Sender saved = senderRepository.save(entity);
        return senderMapper.toDto(saved);
    }
    public SenderDto updateSender(Long senderId, SenderDto senderDto) {
        Sender senderById = findSenderById(senderId);

        // Check if the new name is unique
        String newName = senderDto.getName();
        Sender senderByName = senderRepository.findSenderByName(newName);

        if (senderByName != null && !senderByName.getId().equals(senderId)) {
            // Another sender with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای فرستنده قبلاً ثبت شده است.");
        }

        Sender senderToBeUpdate = senderMapper.partialUpdate(senderDto, senderById);
        Sender updated = senderRepository.save(senderToBeUpdate);
        return senderMapper.toDto(updated);
    }
    public String removeSender(Long senderId){
        boolean result = senderRepository.existsById(senderId);
        if (result){
            if (senderRepository.hasAssociatedLetter(senderId)) {
                throw new IllegalStateException("اشکال! این فرستنده دارای نامه مرتبط می‌باشد و نمی‌تواند حذف شود.");
            }
            senderRepository.deleteById(senderId);
            return "فرستنده با موفقیت حذف شد. ";
        }
        return "خطا در حذف فرستنده.";
    }
    public String getLetterPrefixById(Long senderId) {
        Sender sender = findSenderById(senderId);
        return sender.getLetterPrefix();
    }
    public int getLetterCounterById(Long senderId) {
       return senderRepository.getMaxLetterCountBySenderId(senderId);
    }
    public void incrementLetterCountByOne(Integer count,Long senderId) {
        senderRepository.incrementLetterCountByOne(count,senderId);
    }
    public boolean existById(Long id){
       return !senderRepository.existsById(id);
    }
}
