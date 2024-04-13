package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.mappers.PositionMapper;
import com.pishgaman.phonebook.repositories.PositionRepository;
import com.pishgaman.phonebook.searchforms.PositionSearch;
import com.pishgaman.phonebook.specifications.PositionSpecification;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public Page<PositionDto> findAll(int page, int size, String sortBy, String order, PositionSearch search) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Position> specification = PositionSpecification.getSpecification(search);
        return positionRepository.findAll(specification, pageRequest)
                .map(positionMapper::toDto);
    }
    public List<PositionDto> findAllPositionSelect(String searchParam) {
        Specification<Position> specification = PositionSpecification.getSelectSpecification(searchParam);
        return positionRepository.findAll(specification).stream().map(positionMapper::toDto).collect(Collectors.toList());
    }
    public List<PositionDto> searchPositionByNameContaining(String searchQuery) {
        return positionRepository.findAllByNameContaining(searchQuery).stream().map(positionMapper::toDto).collect(Collectors.toList());
    }

    public String importPositionsFromExcel(MultipartFile file) throws IOException {
        List<PositionDto> positionDtos = ExcelDataImporter.importData(file, PositionDto.class);
        List<Position> positions = positionDtos.stream().map(positionMapper::toEntity).collect(Collectors.toList());
        positionRepository.saveAll(positions);
        return positions.size() + " positions have been imported successfully.";
    }

    public byte[] exportPositionsToExcel() throws IOException {
        List<PositionDto> positionDtos = positionRepository.findAll().stream().map(positionMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(positionDtos, PositionDto.class);
    }

    public byte[] generatePositionTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(PositionDto.class);
    }

    public PositionDto findById(Long positionId) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        if (optionalPosition.isEmpty()) {
            throw new EntityNotFoundException("پست با شناسه : " + positionId + " یافت نشد.");
        }
        return positionMapper.toDto(optionalPosition.get());
    }

    public PositionDto createPosition(PositionDto positionDto) {
        Position entity = positionMapper.toEntity(positionDto);
        Position saved = positionRepository.save(entity);
        return positionMapper.toDto(saved);
    }

    public PositionDto updatePosition(Long positionId, PositionDto positionDto) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        if (optionalPosition.isEmpty()){
            throw new EntityNotFoundException("Position with id" + positionId + " not found");
        }
        Position positionToBeUpdate = positionMapper.partialUpdate(positionDto, optionalPosition.get());
        Position updated = positionRepository.save(positionToBeUpdate);
        return positionMapper.toDto(updated);
    }

    public void deletePosition(Long positionId) {
        if (!positionRepository.existsById(positionId)) {
            throw new EntityNotFoundException("پست با شناسه : " + positionId + " یافت نشد.");
        }
        positionRepository.deleteById(positionId);
    }
}
