package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.TaxPaymentSlipDetailDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import com.pishgaman.phonebook.mappers.TaxPaymentSlipDetailMapper;
import com.pishgaman.phonebook.mappers.TaxPaymentSlipMapper;
import com.pishgaman.phonebook.repositories.TaxPaymentSlipRepository;
import com.pishgaman.phonebook.searchforms.TaxPaymentSlipSearchForm;
import com.pishgaman.phonebook.specifications.TaxPaymentSlipSpecification;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelDataImporter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxPaymentSlipService {
    private final TaxPaymentSlipRepository taxPaymentSlipRepository;
    private final TaxPaymentSlipMapper taxPaymentSlipMapper;
    private final TaxPaymentSlipDetailMapper taxPaymentSlipDetailMapper;

    public String uploadFromExcelFile(MultipartFile file) throws IOException {
        List<TaxPaymentSlipDto> taxPaymentSlipDtos = ExcelDataImporter.importData(file, TaxPaymentSlipDto.class);
        List<TaxPaymentSlip> taxPaymentSlips = taxPaymentSlipDtos.stream().map(taxPaymentSlipMapper::toEntity).collect(Collectors.toList());
        taxPaymentSlipRepository.saveAll(taxPaymentSlips);
        return taxPaymentSlips.size() + " tax payment slips successfully imported.";
    }

    public byte[] exportToExcelFile() throws IOException {
        List<TaxPaymentSlipDto> taxPaymentSlipDtoList = taxPaymentSlipRepository.findAll().stream().map(taxPaymentSlipMapper::toDto).collect(Collectors.toList());
        return ExcelDataExporter.exportData(taxPaymentSlipDtoList, TaxPaymentSlipDto.class);
    }

    public Page<TaxPaymentSlipDetailDto> findAll(TaxPaymentSlipSearchForm search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<TaxPaymentSlip> specification = TaxPaymentSlipSpecification.bySearchForm(search);
        return taxPaymentSlipRepository.findAll(specification, pageRequest)
                .map(taxPaymentSlipDetailMapper::toDto);
    }

    private TaxPaymentSlip findTaxPaymentSlipById(Long taxPaymentSlipId) {
        Optional<TaxPaymentSlip> optionalTaxPaymentSlip = taxPaymentSlipRepository.findById(taxPaymentSlipId);
        if (optionalTaxPaymentSlip.isEmpty()) {
            throw new EntityNotFoundException("Tax payment slip not found with id: " + taxPaymentSlipId);
        }
        return optionalTaxPaymentSlip.get();
    }

    public TaxPaymentSlipDto findById(Long taxPaymentSlipId) {
        return taxPaymentSlipMapper.toDto(findTaxPaymentSlipById(taxPaymentSlipId));
    }

    public TaxPaymentSlipDto createTaxPaymentSlip(TaxPaymentSlipDto taxPaymentSlipDto) {
        TaxPaymentSlip entity = taxPaymentSlipMapper.toEntity(taxPaymentSlipDto);
        TaxPaymentSlip saved = taxPaymentSlipRepository.save(entity);
        return taxPaymentSlipMapper.toDto(saved);
    }

    public TaxPaymentSlipDto updateTaxPaymentSlip(Long taxPaymentSlipId, TaxPaymentSlipDto taxPaymentSlipDto) {
        TaxPaymentSlip taxPaymentSlipById = findTaxPaymentSlipById(taxPaymentSlipId);
        TaxPaymentSlip taxPaymentSlipToBeUpdate = taxPaymentSlipMapper.partialUpdate(taxPaymentSlipDto , taxPaymentSlipById);
        TaxPaymentSlip updated = taxPaymentSlipRepository.save(taxPaymentSlipToBeUpdate);
        return taxPaymentSlipMapper.toDto(updated);
    }

    public String removeTaxPaymentSlip(Long taxPaymentSlipId) {
        boolean result = taxPaymentSlipRepository.existsById(taxPaymentSlipId);
        taxPaymentSlipRepository.deleteById(taxPaymentSlipId);
        return  "Tax payment slip successfully deleted.";
    }

    public boolean existById(Long id){
        return taxPaymentSlipRepository.existsById(id);
    }

    public void saveTaxPaymentSlipFile(Long taxPaymentSlipId, MultipartFile file) throws IOException {
        TaxPaymentSlip taxPaymentSlip = taxPaymentSlipRepository.findById(taxPaymentSlipId)
                .orElseThrow(() -> new EntityNotFoundException("Tax payment slip not found with id: " + taxPaymentSlipId));

        byte[] fileBytes = file.getBytes();
        taxPaymentSlip.setFile(Arrays.copyOf(fileBytes, fileBytes.length));
        taxPaymentSlip.setFileExtension(Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        taxPaymentSlipRepository.save(taxPaymentSlip);
    }
}
