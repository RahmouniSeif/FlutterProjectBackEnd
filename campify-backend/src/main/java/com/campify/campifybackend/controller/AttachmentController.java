package com.campify.campifybackend.controller;
import com.campify.campifybackend.model.Attachment;
import com.campify.campifybackend.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    // GET all attachments
    @GetMapping
    public ResponseEntity<List<Attachment>> getAllAttachments() {
        List<Attachment> attachments = attachmentService.getAllAttachments();
        return ResponseEntity.ok(attachments);
    }

    // GET attachment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachmentById(@PathVariable UUID id) {
        Attachment attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(attachment);
    }

    // GET attachments by User ID (Example of a custom endpoint)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Attachment>> getAttachmentsByUserId(@PathVariable UUID userId) {
        List<Attachment> attachments = attachmentService.getAttachmentsByUserId(userId);
        return ResponseEntity.ok(attachments);
    }

    // POST a new attachment (For file *metadata* creation)
    @PostMapping
    public ResponseEntity<Attachment> createAttachment(@RequestBody Attachment attachment) {
        Attachment savedAttachment = attachmentService.saveAttachment(attachment);
        // Returns 201 Created status
        return new ResponseEntity<>(savedAttachment, HttpStatus.CREATED);
    }

    // PUT (Update) an attachment
    @PutMapping("/{id}")
    public ResponseEntity<Attachment> updateAttachment(
            @PathVariable UUID id,
            @RequestBody Attachment attachmentDetails) {
        Attachment updatedAttachment = attachmentService.updateAttachment(id, attachmentDetails);
        return ResponseEntity.ok(updatedAttachment);
    }

    // DELETE an attachment
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAttachment(@PathVariable UUID id) {
        attachmentService.deleteAttachment(id);
        // Returns 204 No Content status
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}