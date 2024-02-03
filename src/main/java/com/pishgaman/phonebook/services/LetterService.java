package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.mappers.LetterMapper;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.repositories.YearRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LetterService {
    private final LetterRepository letterRepository;
    private final LetterMapper letterMapper;
    private final SenderService senderService;
    private final RecipientService recipientService;
    private final YearRepository yearRepository;


    @Autowired
    public LetterService(LetterRepository letterRepository,
                         LetterMapper letterMapper,
                         SenderService senderService,
                         RecipientService recipientService, YearRepository yearRepository) {
        this.letterRepository = letterRepository;
        this.letterMapper = letterMapper;
        this.senderService = senderService;
        this.recipientService = recipientService;
        this.yearRepository = yearRepository;
    }

    public List<LetterDto> getAllLetters() {
        List<Letter> letters = letterRepository.findAll();
        return letters.stream().map(letterMapper::toDto).collect(Collectors.toList());
    }
    public List<LetterDetailsDto> getLetterDetails() {
        return letterRepository.findLetterDetails();
    }

    public List<LetterDetailsDto> findLetterDetailsBySenderId(Long senderId) {
        return letterRepository.findLetterDetailsBySenderId(senderId);
    }

    public LetterDto getLetterById(Long letterId) {
        Letter letter = findLetterById(letterId);
        return letterMapper.toDto(letter);
    }
    @Transactional
    public void createLetter(LetterDto letterDto) {
        Year year = this.getYear(letterDto.getYearId());
        String letterNumber = this.generateLetterNumber(letterDto.getSenderId(), year.getName());

        if (senderService.existById(letterDto.getSenderId())) throw new IllegalArgumentException("فرستنده با شناسه " + letterDto.getSenderId() + " یافت نشد.");
        if (!recipientService.existById(letterDto.getRecipientId())) throw new IllegalArgumentException("گیرنده با شناسه " + letterDto.getRecipientId() + " یافت نشد.");

        letterRepository.createLetter(
                letterDto.getCreationDate(),
                letterDto.getRecipientId(),
                letterDto.getSenderId(),
                letterDto.getContent(),
                (letterDto.getLetterNumber() == null) ? letterNumber : letterDto.getLetterNumber(),
                year.getId(),
                letterDto.getLetterState().toString()
        );
        int maxLetterCount = senderService.getLetterCounterById(letterDto.getSenderId());
        senderService.incrementLetterCountByOne(maxLetterCount + 1 , letterDto.getSenderId());
    }
    @Transactional
    public void updateLetter(Long letterId, LetterDto letterDto) {

        if (!letterRepository.existsById(letterId)) throw new IllegalArgumentException("نامه با شناسه " + letterId + " یافت نشد.");
        if (senderService.existById(letterDto.getSenderId())) throw new IllegalArgumentException("فرستنده با شناسه " + letterDto.getSenderId() + " یافت نشد.");
        if (!recipientService.existById(letterDto.getRecipientId())) throw new IllegalArgumentException("گیرنده با شناسه " + letterDto.getRecipientId() + " یافت نشد.");
        if (!yearRepository.existsById(letterDto.getYearId())) throw new IllegalArgumentException("سال با شناسه " + letterDto.getYearId() + " یافت نشد.");

        letterRepository.updateLetter(
                letterId,
                letterDto.getCreationDate(),
                letterDto.getRecipientId(),
                letterDto.getSenderId(),
                letterDto.getContent(),
                letterDto.getLetterNumber(),
                letterDto.getYearId(),
                letterDto.getLetterState().toString()
        );
    }

    @Transactional
    public void updateLetterState(Long letterId, LetterState letterState) {


        letterRepository.updateLetterState(letterState, letterId);

        if (letterState.toString().equals("DRAFT")){
            letterRepository.updateDeletable(true,letterId);
        }
        if (letterState.toString().equals("DELIVERED")){
            letterRepository.updateDeletable(false,letterId);
        }

    }

    public String deleteLetter(Long letterId){
        Letter letterById = findLetterById(letterId);
        if (letterById != null){

            letterRepository.deleteById(letterId);
            return "نامه با موفقیت حذف شد. ";
        }
        return "خطا در حذف نامه.";
    }

    private Letter findLetterById(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("نامه با شناسه : " + letterId + " یافت نشد."));
    }

    public String generateLetterNumber(Long senderId,Long yearName) {
        int count = senderService.getLetterCounterById(senderId) + 1;
        String letterPrefix = senderService.getLetterPrefixById(senderId);
        return letterPrefix + "-" + yearName + "-" + count;
    }
    protected Year getYear(Long yearName) {
        Optional<Year> optionalYear = yearRepository.findByYearName(yearName);
        return optionalYear.orElseThrow(() -> new EntityNotFoundException("سال با مقدار " + yearName + " یافت نشد."));
    }
}
