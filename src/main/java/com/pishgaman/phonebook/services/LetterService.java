package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.LetterDetailsDto;
import com.pishgaman.phonebook.dtos.LetterDto;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.enums.LetterState;
import com.pishgaman.phonebook.mappers.LetterMapper;
import com.pishgaman.phonebook.repositories.CompanyRepository;
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
    private final CustomerService customerService;
    private final YearRepository yearRepository;
    private final LetterSearchDao letterSearchDao;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

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
        Optional<Year> optionalYear = yearRepository.findById(letterDto.getYearId());
        Year year = optionalYear.orElseThrow(() -> new EntityNotFoundException("سال با شناسه " + letterDto.getYearId() + " یافت نشد."));
        String letterNumber = this.generateLetterNumber(letterDto.getCompanyId(), year.getName());

        if (!companyService.existById(letterDto.getCompanyId())) {
            throw new IllegalArgumentException("شرکت با شناسه " + letterDto.getCompanyId() + " یافت نشد.");
        }
        if (!customerService.existById(letterDto.getCustomerId())) {
            throw new IllegalArgumentException("گیرنده با شناسه " + letterDto.getCustomerId() + " یافت نشد.");
        }

        letterRepository.createLetter(
                letterDto.getCreationDate(),
                letterDto.getCustomerId(),
                letterDto.getCompanyId(),
                letterDto.getContent(),
                (letterDto.getLetterNumber() == null) ? letterNumber : letterDto.getLetterNumber(),
                year.getId(),
                letterDto.getLetterState().toString(),
                letterDto.getLetterTypeId()
        );
        int maxLetterCount = companyService.getLetterCounterById(letterDto.getCompanyId());
        companyService.incrementLetterCountByOne(maxLetterCount + 1, letterDto.getCompanyId());
    }

    @Transactional
    public void updateLetter(Long letterId, LetterDto letterDto) {
        if (!letterRepository.existsById(letterId)) throw new IllegalArgumentException("نامه با شماره " + letterId + " یافت نشد..");
        if (!companyService.existById(letterDto.getCompanyId())) throw new IllegalArgumentException("شرکت با شناسه " + letterDto.getCompanyId() + " یافت نشد.");
        if (!customerService.existById(letterDto.getCustomerId())) throw new IllegalArgumentException("گیرنده با شناسه " + letterDto.getCustomerId() + " یافت نشد.");
        if (!yearRepository.existsById(letterDto.getYearId())) throw new IllegalArgumentException("سال با شناسه " + letterDto.getYearId() + " یافت نشد.");

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

    @Transactional
    public String deleteLetter(Long letterId) {
        try {
            Letter letterById = findLetterById(letterId);
            if (letterById == null) {
                throw new IllegalStateException("نامه با شناسه " + letterId + " یافت نشد.");
            }
            letterRepository.deleteById(letterId);
            Company company = letterById.getCompany();
            company.setLetterCounter(company.getLetterCounter() - 1);
            companyRepository.save(company);
            return "نامه با شناسه " + letterId + " با موفقیت حذف شد.";
        } catch (Exception e) {
            throw new RuntimeException("خطا در حذف نامه : " + e.getMessage(), e);
        }
    }

    private Letter findLetterById(Long letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new EntityNotFoundException("نامه با شناسه : " + letterId + " یافت نشد."));
    }

    public String generateLetterNumber(Long companyId,Long yearName) {
        Year year = getYear(yearName);
        Long startingLetterNumber = year.getStartingLetterNumber();

        long count = companyService.getLetterCounterById(companyId) + 1 + startingLetterNumber;
        String letterPrefix = companyService.getLetterPrefixById(companyId);
        return yearName + "/" + letterPrefix + "/" + count;
    }
    protected Year getYear(Long yearName) {
        Optional<Year> optionalYear = yearRepository.findByYearName(yearName);
        return optionalYear.orElseThrow(() -> new EntityNotFoundException("سال با مقدار " + yearName + " یافت نشد."));
    }
}
