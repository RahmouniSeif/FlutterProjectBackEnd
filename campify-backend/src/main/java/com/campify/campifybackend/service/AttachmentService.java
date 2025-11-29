package com.campify.campifybackend.service;

import com.campify.campifybackend.model.Attachment;
import com.campify.campifybackend.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    // CREATE
    @Transactional
    public Attachment saveAttachment(Attachment attachment) {
        // You might add business logic here (e.g., file validation, setting default values)
        return attachmentRepository.save(attachment);
    }

    // READ (All)
    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    // READ (By ID)
    public Attachment getAttachmentById(UUID id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + id));
    }

    // READ (By User ID - using the custom method from the repository)
    public List<Attachment> getAttachmentsByUserId(UUID userId) {
        return attachmentRepository.findByUserId(userId);
    }

    // UPDATE
    @Transactional
    public Attachment updateAttachment(UUID id, Attachment attachmentDetails) {
        Attachment existingAttachment = getAttachmentById(id);

        // Update fields (excluding the ID and typically the creation date/user ID)
        existingAttachment.setAttachedFile(attachmentDetails.getAttachedFile());
        existingAttachment.setAttachmentType(attachmentDetails.getAttachmentType());

        // Save and return the updated entity
        return attachmentRepository.save(existingAttachment);
    }

    // DELETE
    @Transactional
    public void deleteAttachment(UUID id) {
        // Check if it exists before deleting
        if (!attachmentRepository.existsById(id)) {
            throw new RuntimeException("Attachment not found with id: " + id);
        }
        attachmentRepository.deleteById(id);
    }
}