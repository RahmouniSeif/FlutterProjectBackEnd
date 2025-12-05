package com.campify.campifybackend.service;



import com.campify.campifybackend.dto.LicenseCheckResponse;
import com.campify.campifybackend.model.AdmLicense;
import com.campify.campifybackend.repository.AdmLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdmLicenseService {

    private final AdmLicenseRepository repository;

    @Autowired
    public AdmLicenseService(AdmLicenseRepository repository) {
        this.repository = repository;
    }

    // --- Standard CRUD Operations ---

    // CREATE / UPDATE
    public AdmLicense save(AdmLicense license) {
        return repository.save(license);
    }

    // READ All
    public List<AdmLicense> findAll() {
        return repository.findAll();
    }

    // READ Single
    public Optional<AdmLicense> findById(UUID id) {
        return repository.findById(id);
    }

    // DELETE
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    // --- Custom CheckLicense Service ---

    /**
     * Checks if a license exists and is not expired yet, returning a detailed status.
     * @param licenseId The ID of the license to check.
     * @return A LicenseCheckResponse with validity status and an error message if invalid.
     */
    public LicenseCheckResponse checkLicense(UUID licenseId) {
        Optional<AdmLicense> licenseOpt = repository.findById(licenseId);

        // 1. Check if the license exists (the 'id is exists' part)
        if (licenseOpt.isEmpty()) {
            return LicenseCheckResponse.builder()
                    .valid(false)
                    .message("Error: License ID [" + licenseId + "] does not exist.")
                    .licenseId(licenseId)
                    .build();
        }

        AdmLicense license = licenseOpt.get();
        LocalDate today = LocalDate.now();

        // 2. Check if the license is expired (the 'is not expired yet' part)
        if (license.getExpireDate() != null && today.isAfter(license.getExpireDate())) {
            return LicenseCheckResponse.builder()
                    .valid(false)
                    .message("Error: License has expired on " + license.getExpireDate() + ".")
                    .licenseId(licenseId)
                    .build();
        }

        // If it exists and is not expired
        return LicenseCheckResponse.builder()
                .valid(true)
                .message("Success: License is valid and active.")
                .licenseId(licenseId)
                .build();
    }
}