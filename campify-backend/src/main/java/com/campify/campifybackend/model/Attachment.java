package com.campify.campifybackend.model;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data; // Consider using Lombok for boilerplate code
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "attachment")
@Data // Generates getters, setters, toString, equals, and hashCode
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Use UUID for foreign keys
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "attached_file", nullable = false)
    private String attachedFile;

    // Maps to TIMESTAMP WITH TIME ZONE. ZonedDateTime is recommended for time-zone awareness.
    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createdDate;

    @Column(name = "attachment_type", length = 50)
    private String attachmentType;

    public Attachment(UUID id, UUID userId, String attachedFile, ZonedDateTime createdDate, String attachmentType) {
        this.id = id;
        this.userId = userId;
        this.attachedFile = attachedFile;
        this.createdDate = createdDate;
        this.attachmentType = attachmentType;
    }
}