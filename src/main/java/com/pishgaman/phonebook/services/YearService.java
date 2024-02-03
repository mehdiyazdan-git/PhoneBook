package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.YearDto;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.exceptions.DatabaseIntegrityViolationException;
import com.pishgaman.phonebook.mappers.YearMapper;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.repositories.YearRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YearService {
    private final YearRepository yearRepository;
    private final YearMapper yearMapper;

    private final LetterRepository letterRepository;
    @Autowired
    public YearService(YearRepository yearRepository, YearMapper yearMapper, LetterRepository letterRepository) {
        this.yearRepository = yearRepository;
        this.yearMapper = yearMapper;
        this.letterRepository = letterRepository;
    }

    public YearDto createYear(YearDto yearDto) {
        Year year = yearMapper.toEntity(yearDto);
        year = yearRepository.save(year);
        return yearMapper.toDto(year);
    }


    public YearDto getYearById(Long id) {
        Year year = yearRepository.findById(id).orElse(null);
        return (year != null) ? yearMapper.toDto(year) : null;
    }


    public List<YearDto> getAllYears() {
        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(yearMapper::toDto)
                .collect(Collectors.toList());
    }


    public YearDto updateYear(Long id, YearDto yearDto) {
        Year year = yearRepository.findById(id).orElse(null);
        if (year != null) {
            yearMapper.partialUpdate(yearDto, year);
            year = yearRepository.save(year);
            return yearMapper.toDto(year);
        }
        return null; // Handle not found scenario
    }


    public void deleteYear(Long id) {
        Optional<Year> optionalYear = yearRepository.findById(id);
        if (optionalYear.isEmpty()) {
            throw new EntityNotFoundException("سال با شناسه " + id + "یافت نشد.");
        }
        Year year = optionalYear.get();

        if (letterRepository.countByYearId(id) > 0) {
            throw new DatabaseIntegrityViolationException("امکان حذف سال وجود ندارد چون نامه های مرتبط دارد.");
        }
        yearRepository.deleteById(id);
    }
}
