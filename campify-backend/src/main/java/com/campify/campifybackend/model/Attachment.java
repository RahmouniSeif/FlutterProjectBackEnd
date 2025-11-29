package com.campify.campifybackend.model;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data; // Consider using Lombok for boilerplate code

@Entity
@Table(name = "attachment")
@Data // Generates getters, setters, toString, equals, and hashCode
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Use UUID for foreign keys
    @Column(name = "userId", nullable = false)
    private UUID userId;

    // TEXT in SQL is mapped to String in Java. This column holds the file path/reference.
    // **Note**: For actual file content, you would typically store the file on a
    // file system/cloud storage and store the reference (path/URL) here,
    // which is what 'attachedFile TEXT' suggests.
    @Column(name = "attachedFile", nullable = false)
    private String attachedFile;

    // Maps to TIMESTAMP WITH TIME ZONE. ZonedDateTime is recommended for time-zone awareness.
    @Column(name = "createdDate", nullable = false)
    private ZonedDateTime createdDate;

    @Column(name = "attachmentType", length = 50)
    private String attachmentType;

    // --- Constructor (Lombok handles this with @Data, but good for clarity) ---
    // You might also need a no-args constructor for JPA, which @Data provides.
    // If you don't use Lombok:
    // public Attachment() {}
    // public Attachment(UUID userId, String attachedFile, ZonedDateTime createdDate, String attachmentType) { ... }
}