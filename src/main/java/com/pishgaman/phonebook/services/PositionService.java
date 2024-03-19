package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.PositionDto;
import com.pishgaman.phonebook.entities.Position;
import com.pishgaman.phonebook.exceptions.EntityAlreadyExistsException;
import com.pishgaman.phonebook.mappers.PositionMapper;
import com.pishgaman.phonebook.repositories.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionService {
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    @Autowired
    public PositionService(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    public List<PositionDto> findAll() {
        return positionRepository.findAll().stream().map(positionMapper::toDto).collect(Collectors.toList());
    }

    public PositionDto findPositionById(Long positionId) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        if (optionalPosition.isEmpty()) {
            throw new EntityNotFoundException("موقعیت با شناسه : " + positionId + " یافت نشد.");
        }
        return positionMapper.toDto(optionalPosition.get());
    }

    public PositionDto createPosition(PositionDto positionDto) {
        Position positionByName = positionRepository.findPositionByName(positionDto.getName());
        if (positionByName != null) {
            throw new EntityAlreadyExistsException("اشکال! موقعیت با نام '" + positionDto.getName() + "' قبلاً ثبت شده است.");
        }
        Position entity = positionMapper.toEntity(positionDto);
        Position saved = positionRepository.save(entity);
        return positionMapper.toDto(saved);
    }

    public PositionDto updatePosition(Long positionId, PositionDto positionDto) {
        Position positionById = positionMapper.toEntity(findPositionById(positionId));

        // Check if the new name is unique
        String newName = positionDto.getName();
        Position positionByName = positionRepository.findPositionByName(newName);

        if (positionByName != null && !positionByName.getId().equals(positionId)) {
            // Another position with the same name already exists
            throw new EntityAlreadyExistsException("اشکال! نام '" + newName + "' برای موقعیت قبلاً ثبت شده است.");
        }

        Position positionToBeUpdated = positionMapper.partialUpdate(positionDto, positionById);
        Position updated = positionRepository.save(positionToBeUpdated);
        return positionMapper.toDto(updated);
    }

    public String removePosition(Long positionId) {
        if (positionRepository.existsById(positionId)) {
            // Add any additional checks here, such as if the position is used in a BoardMember
            positionRepository.deleteById(positionId);
            return "موقعیت با موفقیت حذف شد.";
        } else {
            throw new EntityNotFoundException("موقعیت با شناسه : " + positionId + " یافت نشد.");
        }
    }

    public boolean existById(Long id) {
        return positionRepository.existsById(id);
    }

    // Additional methods specific to the Position can be added here
}
