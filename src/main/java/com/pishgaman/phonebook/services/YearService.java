package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.YearDto;
import com.pishgaman.phonebook.entities.Year;
import com.pishgaman.phonebook.mappers.YearMapper;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YearService {
    private final YearRepository yearRepository;
    private final YearMapper yearMapper;

    public Page<YearDto> findAll(int page, int size, String sortBy, String order, YearSearch search) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Year> specification = YearSpecification.getSpecification(search);
        return yearRepository.findAll(specification, pageRequest)
                .map(yearMapper::toDto);
    }

    public YearDto findById(Long yearId) {
        Optional<Year> optionalYear = yearRepository.findById(yearId);
        if (optionalYear.isEmpty()) {
            throw new EntityNotFoundException("سال با شناسه : " + yearId + " یافت نشد.");
        }
        return yearMapper.toDto(optionalYear.get());
    }

    public YearDto createYear(YearDto yearDto) {
        Year entity = yearMapper.toEntity(yearDto);
        Year saved = yearRepository.save(entity);
        return yearMapper.toDto(saved);
    }

    public YearDto updateYear(Long yearId, YearDto yearDto) {
        Optional<Year> optionalYear = yearRepository.findById(yearId);
        if (optionalYear.isEmpty()){
            throw new EntityNotFoundException("year with id" + yearId + " not found");
        }
        Year yearToBeUpdate = yearMapper.partialUpdate(yearDto, optionalYear.get());
        Year updated = yearRepository.save(yearToBeUpdate);
        return yearMapper.toDto(updated);
    }

    public void deleteYear(Long yearId) {
        if (!yearRepository.existsById(yearId)) {
            throw new EntityNotFoundException("سال با شناسه : " + yearId + " یافت نشد.");
        }
        yearRepository.deleteById(yearId);
    }
}
