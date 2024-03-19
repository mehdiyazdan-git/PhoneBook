package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.CompanyDto;
import com.pishgaman.phonebook.dtos.CompanySelect;
import com.pishgaman.phonebook.dtos.CustomerSelect;
import com.pishgaman.phonebook.entities.Company;
import com.pishgaman.phonebook.entities.Sender;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.CompanyMapper;
import com.pishgaman.phonebook.repositories.CompanyRepository;
import com.pishgaman.phonebook.searchforms.CompanySearch;
import com.pishgaman.phonebook.specifications.CompanySpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public Page<CompanyDto> findAll(CompanySearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Company> specification = CompanySpecification.getSpecification(search);
        return companyRepository.findAll(specification, pageRequest)
                .map(companyMapper::toDto);
    }
    public List<CompanySelect> findAllCompanySelect(String searchParam) {
        Specification<Company> specification = CompanySpecification.getSelectSpecification(searchParam);
        return companyRepository.findAll(specification).stream().map(companyMapper::toSelectDto).collect(Collectors.toList());
    }

    public List<CompanySelect> searchCompanyByNameContaining(String searchQuery) {
        return companyRepository.findByCompanyNameContains(searchQuery).stream().map(companyMapper::toSelectDto).collect(Collectors.toList());
    }
    public int getLetterCounterById(Long companyId) {
        return companyRepository.getMaxLetterCountByCompanyId(companyId);
    }

    private Company findCompanyById(Long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isEmpty()) {
            throw new EntityNotFoundException("شرکت با شناسه : " + companyId + " یافت نشد.");
        }
        return optionalCompany.get();
    }

    public CompanyDto findById(Long companyId) {
        return companyMapper.toDto(findCompanyById(companyId));
    }

    public CompanyDto createCompany(CompanyDto companyDto) {
        Company companyByName = companyRepository.findCompanyByCompanyName(companyDto.getCompanyName());
        if (companyByName != null) {
            throw new EntityAlreadyExistsException("اشکال! شرکت با نام '" + companyDto.getCompanyName() + "' قبلاً ثبت شده است.");
        }
        Company entity = companyMapper.toEntity(companyDto);
        Company saved = companyRepository.save(entity);
        return companyMapper.toDto(saved);
    }

    public CompanyDto updateCompany(Long companyId, CompanyDto companyDto) {
        Company companyById = findCompanyById(companyId);

        // Check if the new name is unique
        String newName = companyDto.getCompanyName();
        Company companyByName = companyRepository.findCompanyByCompanyName(newName);

        if (companyByName != null && !companyByName.getId().equals(companyId)) {
            // Another company with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای شرکت قبلاً ثبت شده است.");
        }

        Company companyToBeUpdate = companyMapper.partialUpdate(companyDto, companyById);
        Company updated = companyRepository.save(companyToBeUpdate);
        return companyMapper.toDto(updated);
    }
    public void incrementLetterCountByOne(Integer count,Long senderId) {
        companyRepository.incrementLetterCountByOne(count,senderId);
    }
    public String getLetterPrefixById(Long companyId) {
        Company company = findCompanyById(companyId);
        return company.getLetterPrefix();
    }

    public String removeCompany(Long companyId) {
        boolean result = companyRepository.existsById(companyId);
        if (result) {
            companyRepository.deleteById(companyId);
            return "شرکت با موفقیت حذف شد.";
        }

        return "خطا در حذف شرکت.";
    }

    public boolean existById(Long id) {
        return companyRepository.existsById(id);
    }
}
