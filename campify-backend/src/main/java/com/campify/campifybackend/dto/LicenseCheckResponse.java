package com.campify.campifybackend.dto;
import lombok.Data;
import lombok.Builder;
import java.util.UUID;

@Data
@Builder
public class LicenseCheckResponse {
    private boolean valid;
    private String message;
    private UUID licenseId;
}