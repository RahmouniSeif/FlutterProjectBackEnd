package com.campify.campifybackend.repository;

import com.campify.campifybackend.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    // Custom finder method: Find all attachments belonging to a specific user
    List<Attachment> findByUserId(UUID userId);
}