package com.pishgaman.phonebook.services;


import com.pishgaman.phonebook.dtos.PersonDto;
import com.pishgaman.phonebook.dtos.ShareholderDetailDto;
import com.pishgaman.phonebook.dtos.ShareholderDto;
import com.pishgaman.phonebook.entities.Shareholder;
import com.pishgaman.phonebook.mappers.ShareholderDetailMapper;
import com.pishgaman.phonebook.mappers.ShareholderMapper;
import com.pishgaman.phonebook.repositories.ShareholderRepository;
import com.pishgaman.phonebook.searchforms.ShareholderSearchForm;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.ShareholderSpecification;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ShareHolderService {
    private final ShareholderRepository shareHolderRepository;
    private final ShareholderMapper shareHolderMapper;
    private final ShareholderDetailMapper shareholderDetailMapper;
    private final DateConvertor dateConvertor;
    private final UserRepository userRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

    public String uploadFromExcelFile(MultipartFile file) throws IOException {
        List<ShareholderDto> shareholderDtos = ExcelDataImporter.importData(file, ShareholderDto.class);
        List<Shareholder> shareholders = shareholderDtos.stream().map(shareHolderMapper::toEntity).collect(Collectors.toList());
        shareHolderRepository.saveAll(shareholders);
        return shareholders.size() + " سهامدار با موفقیت وارد شدند.";
    }


    public byte[] exportToExcelFile(Long companyId) throws IOException {
        List<ShareholderDto> shareholderDtoList = shareHolderRepository
                .findAllByCompanyId(companyId)
                .stream()
                .map(shareHolderMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(shareholderDtoList, ShareholderDto.class);
    }

    public Page<ShareholderDetailDto> findAll(
            ShareholderSearchForm search,
            int page,
            int size,
            String sortBy,
            String order
    ) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(order), sortBy);
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Specification<Shareholder> specification = ShareholderSpecification.bySearchForm(search);
            return shareHolderRepository.findAll(specification, pageRequest)
                    .map(shareholderDetailMapper::toDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while fetching shareholders", e);
        }
    }



    private Shareholder findShareHolderById(Long ShareHolderId) {
        Optional<Shareholder> optionalShareHolder = shareHolderRepository.findById(ShareHolderId);
        if (optionalShareHolder.isEmpty()) {
            throw new EntityNotFoundException("مشتری با شناسه : " + ShareHolderId + " یافت نشد.");
        }
        return optionalShareHolder.get();
    }
    public String importShareHoldersFromExcel(MultipartFile file) throws IOException {
        List<ShareholderDto> shareHolderDtos = ExcelDataImporter.importData(file, ShareholderDto.class);
        List<Shareholder> shareHolders = shareHolderDtos.stream().map(shareHolderMapper::toEntity).collect(Collectors.toList());
        shareHolderRepository.saveAll(shareHolders);
        return shareHolders.size() + " shareholders have been imported successfully.";
    }

    public byte[] exportShareHoldersToExcel() throws IOException {
        List<ShareholderDto> shareHolderDtos = shareHolderRepository.findAll().stream().map(shareHolderMapper::toDto)
                .collect(Collectors.toList());
        return ExcelDataExporter.exportData(shareHolderDtos, ShareholderDto.class);
    }

    public byte[] generateShareholderTemplate() throws IOException {
        return ExcelTemplateGenerator.generateTemplateExcel(ShareholderDto.class);
    }

    public ShareholderDto findById(Long ShareHolderId) {
        ShareholderDto dto = shareHolderMapper.toDto(findShareHolderById(ShareHolderId));
        dto.setCreateByFullName(getFullName(dto.getCreatedBy()));
        dto.setLastModifiedByFullName(getFullName(dto.getLastModifiedBy()));
        dto.setCreateAtJalali(dateConvertor.convertGregorianToJalali(dto.getCreatedDate()));
        dto.setLastModifiedAtJalali(dateConvertor.convertGregorianToJalali(dto.getLastModifiedDate()));
        return dto;
    }

    public ShareholderDto createShareHolder(ShareholderDto ShareHolderDto) {
        Shareholder entity = shareHolderMapper.toEntity(ShareHolderDto);
        Shareholder saved = shareHolderRepository.save(entity);
        return shareHolderMapper.toDto(saved);
    }

    public ShareholderDto updateShareHolder(Long shareHolderId, ShareholderDto shareholderDto) {
        Shareholder ShareHolderById = findShareHolderById(shareHolderId);

        Shareholder ShareHolderToBeUpdate = shareHolderMapper.partialUpdate(shareholderDto , ShareHolderById);
        Shareholder updated = shareHolderRepository.save(ShareHolderToBeUpdate);
        return shareHolderMapper.toDto(updated);
    }

    public String removeShareHolder(Long ShareHolderId) {
        boolean result = shareHolderRepository.existsById(ShareHolderId);
        shareHolderRepository.deleteById(ShareHolderId);
        return  "سهامدار با موفقیت حذف شد." ;
    }
    public boolean existById(Long id){
        return shareHolderRepository.existsById(id);
    }

    public void saveShareholderFile(
            Long shareholderId,
            MultipartFile file,
            String fileName,
            String fileExtension
    ) throws IOException {
        Shareholder shareholder = shareHolderRepository.findById(shareholderId)
                .orElseThrow(() -> new EntityNotFoundException("Shareholder not found with id: " + shareholderId));

        byte[] fileBytes = file.getBytes();

        if (fileName == null || fileName.isEmpty()) {
            // Extract file name from the original file
            fileName = Objects.requireNonNull(file.getOriginalFilename());
        }
        if (fileExtension == null || fileExtension.isEmpty()) {
            // Extract file extension from the original file
            fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        }

        shareholder.setScannedShareCertificate(Arrays.copyOf(fileBytes, fileBytes.length));
        shareholder.setFileName(fileName);
        shareholder.setFileExtension(fileExtension);
        shareHolderRepository.save(shareholder);
    }

    public String deleteShareholderFile(Long shareholderId) {
        Shareholder shareholder = shareHolderRepository.findById(shareholderId)
                .orElseThrow(() -> new EntityNotFoundException("Shareholder not found with id: " + shareholderId));
        shareholder.setScannedShareCertificate(null);
        shareholder.setFileName(null);
        shareholder.setFileExtension(null);
        shareHolderRepository.save(shareholder);

        return "The file for the shareholder has been successfully deleted.";
    }

}

