package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.InsuranceSlipDetailDto;
import com.pishgaman.phonebook.dtos.InsuranceSlipDto;
import com.pishgaman.phonebook.entities.InsuranceSlip;
import com.pishgaman.phonebook.mappers.InsuranceSlipDetailMapper;
import com.pishgaman.phonebook.mappers.InsuranceSlipMapper;
import com.pishgaman.phonebook.repositories.InsuranceSlipRepository;
import com.pishgaman.phonebook.searchforms.InsuranceSlipSearchForm;
import com.pishgaman.phonebook.specifications.InsuranceSlipSpecification;
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
public class InsuranceSlipService {
    private final InsuranceSlipRepository insuranceSlipRepository;
    private final InsuranceSlipMapper insuranceSlipMapper;
    private final InsuranceSlipDetailMapper insuranceSlipDetailMapper;

    public String uploadFromExcelFile(MultipartFile file) throws IOException {
        List<InsuranceSlipDto> insuranceSlipDtos = ExcelDataImporter.importData(file, InsuranceSlipDto.class);
        List<InsuranceSlip> insuranceSlips = insuranceSlipDtos.stream().map(insuranceSlipMapper::toEntity).collect(Collectors.toList());
        insuranceSlipRepository.saveAll(insuranceSlips);
        return insuranceSlips.size() + " insurance slips successfully imported.";
    }

    public byte[] exportToExcelFile() throws IOException {
        List<InsuranceSlipDto> insuranceSlipDtoList = insuranceSlipRepository.findAll().stream().map(insuranceSlipMapper::toDto).collect(Collectors.toList());
        return ExcelDataExporter.exportData(insuranceSlipDtoList, InsuranceSlipDto.class);
    }

    public Page<InsuranceSlipDetailDto> findAll(InsuranceSlipSearchForm search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<InsuranceSlip> specification = InsuranceSlipSpecification.bySearchForm(search);
        return insuranceSlipRepository.findAll(specification, pageRequest)
                .map(insuranceSlipDetailMapper::toDto);
    }

    private InsuranceSlip findInsuranceSlipById(Long insuranceSlipId) {
        Optional<InsuranceSlip> optionalInsuranceSlip = insuranceSlipRepository.findById(insuranceSlipId);
        if (optionalInsuranceSlip.isEmpty()) {
            throw new EntityNotFoundException("Insurance slip not found with id: " + insuranceSlipId);
        }
        return optionalInsuranceSlip.get();
    }

    public InsuranceSlipDto findById(Long insuranceSlipId) {
        return insuranceSlipMapper.toDto(findInsuranceSlipById(insuranceSlipId));
    }

    public InsuranceSlipDto createInsuranceSlip(InsuranceSlipDto insuranceSlipDto) {
        InsuranceSlip entity = insuranceSlipMapper.toEntity(insuranceSlipDto);
        InsuranceSlip saved = insuranceSlipRepository.save(entity);
        return insuranceSlipMapper.toDto(saved);
    }

    public InsuranceSlipDto updateInsuranceSlip(Long insuranceSlipId, InsuranceSlipDto insuranceSlipDto) {
        InsuranceSlip insuranceSlipById = findInsuranceSlipById(insuranceSlipId);

        InsuranceSlip insuranceSlipToBeUpdate = insuranceSlipMapper.partialUpdate(insuranceSlipDto , insuranceSlipById);
        InsuranceSlip updated = insuranceSlipRepository.save(insuranceSlipToBeUpdate);
        return insuranceSlipMapper.toDto(updated);
    }

    public String removeInsuranceSlip(Long insuranceSlipId) {
        boolean result = insuranceSlipRepository.existsById(insuranceSlipId);
        insuranceSlipRepository.deleteById(insuranceSlipId);
        return "Insurance slip successfully deleted.";
    }

    public boolean existById(Long id){
        return insuranceSlipRepository.existsById(id);
    }

    public void saveInsuranceSlipFile(Long insuranceSlipId, MultipartFile file) throws IOException {
        InsuranceSlip insuranceSlip = insuranceSlipRepository.findById(insuranceSlipId)
                .orElseThrow(() -> new EntityNotFoundException("Insurance slip not found with id: " + insuranceSlipId));

        byte[] fileBytes = file.getBytes();
        insuranceSlip.setFile(Arrays.copyOf(fileBytes, fileBytes.length));
        insuranceSlip.setFileExtension(Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        insuranceSlipRepository.save(insuranceSlip);
    }
}
