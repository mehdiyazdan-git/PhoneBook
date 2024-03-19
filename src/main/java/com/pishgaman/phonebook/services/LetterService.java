package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.mappers.LetterMapper;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.repositories.LetterSearchDao;
import com.pishgaman.phonebook.repositories.YearRepository;
import com.pishgaman.phonebook.searchforms.LetterSearch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;
    private final LetterMapper letterMapper;
    private final SenderService senderService;
    private final CustomerService customerService;
    private final YearRepository yearRepository;
    private final LetterSearchDao letterSearchDao;
    private final CompanyService companyService;

    public List<LetterDto> getAllLetters() {
        List<Letter> letters = letterRepository.findAll();
        return letters.stream().map(letterMapper::toDto).collect(Collectors.toList());
    }

    public Page<LetterDetailsDto> findAllLetterDetails(
            LetterSearch search,
            Integer page,
            Integer size,
            String sortBy,
            String order
    ) {
        return letterSearchDao.findAllBySimpleQuery(search,page,size,sortBy,order);
    }
//    public List<LetterDetailsDto> getLetterDetails() {
//        return letterRepository.findLetterDetails();
//    }

//    public List<LetterDetailsDto> findLetterDetailsBySenderId(Long senderId) {
//        return letterRepository.findLetterDetailsBySenderId(senderId);
//    }

    public LetterDto getLetterById(Long letterId) {
        Letter letter = findLetterById(letterId);
        return letterMapper.toDto(letter);
    }
    @Transactional
    public void createLetter(LetterDto letterDto) {
        Year year = this.getYear(letterDto.getYearId());
        String letterNumber = this.generateLetterNumber(letterDto.getCompanyId(), year.getName());

        if (companyService.existById(letterDto.getCompanyId())) throw new IllegalArgumentException("Company with id " + letterDto.getCompanyId() + " not found.");
        if (!customerService.existById(letterDto.getCustomerId())) throw new IllegalArgumentException("Recipient with id " + letterDto.getCustomerId() + " not found.");

        letterRepository.createLetter(
                letterDto.getCreationDate(),
                letterDto.getCustomerId(),
                letterDto.getCompanyId(),
                letterDto.getContent(),
                (letterDto.getLetterNumber() == null) ? letterNumber : letterDto.getLetterNumber(),
                year.getId(),
                letterDto.getLetterState().toString()
        );
        int maxLetterCount = companyService.getLetterCounterById(letterDto.getCompanyId());
        companyService.incrementLetterCountByOne(maxLetterCount + 1, letterDto.getCompanyId());
    }

    @Transactional
    public void updateLetter(Long letterId, LetterDto letterDto) {
        if (!letterRepository.existsById(letterId)) throw new IllegalArgumentException("Letter with id " + letterId + " not found.");
        if (companyService.existById(letterDto.getCompanyId())) throw new IllegalArgumentException("Company with id " + letterDto.getCompanyId() + " not found.");
        if (!customerService.existById(letterDto.getCustomerId())) throw new IllegalArgumentException("Recipient with id " + letterDto.getCustomerId() + " not found.");
        if (!yearRepository.existsById(letterDto.getYearId())) throw new IllegalArgumentException("Year with id " + letterDto.getYearId() + " not found.");

        letterRepository.updateLetter(
                letterId,
                letterDto.getCreationDate(),
                letterDto.getCustomerId(),
                letterDto.getCompanyId(),
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

    public String generateLetterNumber(Long companyId,Long yearName) {
        int count = companyService.getLetterCounterById(companyId) + 1;
        String letterPrefix = companyService.getLetterPrefixById(companyId);
        return letterPrefix + "-" + yearName + "-" + count;
    }
    protected Year getYear(Long yearName) {
        Optional<Year> optionalYear = yearRepository.findByYearName(yearName);
        return optionalYear.orElseThrow(() -> new EntityNotFoundException("سال با مقدار " + yearName + " یافت نشد."));
    }
}
