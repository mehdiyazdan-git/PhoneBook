package com.pishgaman.phonebook.services;

import com.pishgaman.phonebook.dtos.AttachListDto;
import com.pishgaman.phonebook.dtos.AttachmentDto;
import com.pishgaman.phonebook.entities.Attachment;
import com.pishgaman.phonebook.mappers.AttachmentMapper;
import com.pishgaman.phonebook.repositories.AttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository, AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
    }
    @Transactional(readOnly = true)
    public byte[] getAttachmentById(Long id) throws IOException {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));
        return attachment.getFileContent();
    }

    public List<AttachListDto> findAllByLetterId(Long letterId) {
        return attachmentRepository.findAllByLetterId(letterId);
    }
    private Attachment findAttachmentById(Long attachId) {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(attachId);
        if (optionalAttachment.isEmpty()) {
            throw new EntityNotFoundException("گیرنده با شناسه : " + attachId + " یافت نشد.");
        }
        return optionalAttachment.get();
    }

    public Attachment findById(Long attachId) {
        return findAttachmentById(attachId);
    }

    public AttachmentDto createAttachment(AttachmentDto attachmentDto) {
        Attachment entity = attachmentMapper.toEntity(attachmentDto);
        Attachment saved = attachmentRepository.save(entity);
        return attachmentMapper.toDto(saved);
    }

    public AttachmentDto updateAttachment(Long attachId, AttachmentDto attachmentDto) {
        Attachment attachmentById = findAttachmentById(attachId);
        Attachment attachmentToBeUpdate = attachmentMapper.partialUpdate(attachmentDto, attachmentById);
        Attachment updated = attachmentRepository.save(attachmentToBeUpdate);
        return attachmentMapper.toDto(updated);
    }

    public String removeAttachment(Long attachId) {
        boolean result = attachmentRepository.existsById(attachId);
        if (result){
            attachmentRepository.deleteById(attachId);
            return  "الصاق با موفقیت حذف شد." ;
        }

        return "خطا در حذف الصاق.";
    }

}
