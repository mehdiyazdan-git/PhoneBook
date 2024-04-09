package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.YearDto;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.exceptions.DatabaseIntegrityViolationException;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.YearMapper;
import com.pishgaman.phonebook.repositories.LetterRepository;
import com.pishgaman.phonebook.repositories.YearRepository;
import com.pishgaman.phonebook.searchforms.YearSearch;
import com.pishgaman.phonebook.specifications.YearSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YearService {
    private final YearRepository yearRepository;
    private final YearMapper yearMapper;
    private final LetterRepository letterRepository;

    public Page<YearDto> findAll(int page, int size, String sortBy, String order, YearSearch search) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Year> specification = YearSpecification.getSpecification(search);
        return yearRepository.findAll(specification, pageRequest)
                .map(yearMapper::toDto);
    }
    public List<YearDto> searchYearByNameContaining(Long searchQuery) {
        return yearRepository
                .findByYearName(searchQuery)
                .stream()
                .map(yearMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<YearDto> findAllYearSelect(YearSearch searchParam) {
        Specification<Year> specification = YearSpecification.getSpecification(searchParam);
        return yearRepository
                .findAll(specification)
                .stream()
                .map(yearMapper::toDto)
                .collect(Collectors.toList());
    }


    public YearDto findById(Long yearId) {
        Optional<Year> optionalYear = yearRepository.findById(yearId);
        if (optionalYear.isEmpty()) {
            throw new EntityNotFoundException("سال با شناسه : " + yearId + " یافت نشد.");
        }
        return yearMapper.toDto(optionalYear.get());
    }

    public YearDto createYear(YearDto yearDto) {
        Optional<Year> year = yearRepository.findYearByName(yearDto.getName());
        
        if (year.isPresent()) {
            throw new EntityAlreadyExistsException("سال با نام  : " + yearDto.getName() + " وجود دارد.");
        }
        return yearMapper.toDto(yearRepository.save(yearMapper.toEntity(yearDto)));
    }

    public YearDto updateYear(Long yearId, YearDto yearDto) {
        Optional<Year> optionalYear = yearRepository.findById(yearId);
        if (optionalYear.isEmpty()) {
            throw new EntityNotFoundException("سال با شناسه : " + yearId + " یافت نشد.");
        }
        if (yearRepository.findYearByNameAndIdNot(yearDto.getName(),yearId).isPresent()) {
            throw new EntityAlreadyExistsException("سال با نام  : " + yearDto.getName() + " وجود دارد.");
        }
        Year year = optionalYear.get();
        Year partialUpdate = yearMapper.partialUpdate(yearDto, year);

        return yearMapper.toDto(yearRepository.save(partialUpdate));
    }

    public void deleteYear(Long yearId) {
        if (!yearRepository.existsById(yearId)) {
            throw new EntityNotFoundException("سال با شناسه : " + yearId + " یافت نشد.");
        }
        if (letterRepository.existsByYearId(yearId)){
            throw new DatabaseIntegrityViolationException("امکان حذف وجود ندارد زیرا نامه های مرتبط دارد.");
        }
        yearRepository.deleteById(yearId);
    }
}
