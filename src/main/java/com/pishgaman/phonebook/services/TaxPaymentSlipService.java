package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDetailDto;
import com.pishgaman.phonebook.dtos.TaxPaymentSlipDto;
import com.pishgaman.phonebook.entities.TaxPaymentSlip;
import com.pishgaman.phonebook.mappers.TaxPaymentSlipDetailMapper;
import com.pishgaman.phonebook.mappers.TaxPaymentSlipMapper;
import com.pishgaman.phonebook.repositories.TaxPaymentSlipRepository;
import com.pishgaman.phonebook.searchforms.TaxPaymentSlipSearchForm;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.TaxPaymentSlipSpecification;
import com.pishgaman.phonebook.utils.DateConvertor;
import com.pishgaman.phonebook.utils.ExcelDataExporter;
import com.pishgaman.phonebook.utils.ExcelDataImporter;
import com.pishgaman.phonebook.utils.ExcelTemplateGenerator;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxPaymentSlipService {
    private final TaxPaymentSlipRepository taxPaymentSlipRepository;
    private final TaxPaymentSlipMapper taxPaymentSlipMapper;
    private final TaxPaymentSlipDetailMapper taxPaymentSlipDetailMapper;
    private final UserRepository userRepository;
    private final DateConvertor dateConvertor;

    public String uploadFromExcelFile(MultipartFile file) throws IOException {
        List<TaxPaymentSlipDto> taxPaymentSlipDtos = ExcelDataImporter.importData(file, TaxPaymentSlipDto.class);
        List<TaxPaymentSlip> taxPaymentSlips = taxPaymentSlipDtos.stream().map(taxPaymentSlipMapper::toEntity).collect(Collectors.toList());
        taxPaymentSlipRepository.saveAll(taxPaymentSlips);
        return taxPaymentSlips.size() + " tax payment slips successfully imported.";
    }
    public String importTaxPaymentSlipsFromExcel(MultipartFile file) throws IOException {
        List<TaxPaymentSlipDto> taxPaymentSlipDtos = ExcelDataImporter.importData(file, TaxPaymentSlipDto.class);
        List<TaxPaymentSlip> taxPaymentSlips = taxPaymentSlipDtos.stream().map(taxPaymentSlipMapper::toEntity).collect(Collectors.toList());
        taxPaymentSlipRepository.saveAll(taxPaymentSlips);
        return taxPaymentSlips.size() + " tax payment slips have been imported successfully.";
    }

    public byte[] exportTaxPaymentSlipsToExcel() throws IOException {
        List<TaxPaymentSlipDto> taxPaymentSlipDtos = taxPaymentSlipRepository.findAll().stream().map(taxPaymentSlipMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(taxPaymentSlipDtos, TaxPaymentSlipDto.class);
    }

    public byte[] generateTaxPaymentSlipTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(TaxPaymentSlipDto.class);
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

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

    public TaxPaymentSlipDto findById(Long taxPaymentSlipId) {
        TaxPaymentSlipDto dto = taxPaymentSlipMapper.toDto(findTaxPaymentSlipById(taxPaymentSlipId));
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
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

    public void saveTaxPaymentSlipFile(
            Long taxPaymentSlipId,
            MultipartFile file,
            String fileName,
            String fileExtension
    ) throws IOException {
        TaxPaymentSlip taxPaymentSlip = taxPaymentSlipRepository.findById(taxPaymentSlipId)
                .orElseThrow(() -> new EntityNotFoundException("Tax payment slip not found with id: " + taxPaymentSlipId));

        byte[] fileBytes = file.getBytes();
        taxPaymentSlip.setFile(Arrays.copyOf(fileBytes, fileBytes.length));
        taxPaymentSlip.setFileExtension(fileExtension);
        taxPaymentSlip.setFileName(fileName);
        taxPaymentSlipRepository.save(taxPaymentSlip);
    }

    public String deleteTaxPaymentSlipFile(Long taxPaymentSlipId) {
        TaxPaymentSlip taxPaymentSlip = taxPaymentSlipRepository.findById(taxPaymentSlipId)
                .orElseThrow(() -> new EntityNotFoundException("فیش مالیات با شناسه " + taxPaymentSlipId + " یافت نشد."));
        taxPaymentSlip.setFile(null);
        taxPaymentSlip.setFileName(null);
        taxPaymentSlip.setFileExtension(null);
        taxPaymentSlipRepository.save(taxPaymentSlip);
        return "فایل فیش مالیات با شناسه " + taxPaymentSlipId + " با موفقیت حذف شد.";
    }
}
