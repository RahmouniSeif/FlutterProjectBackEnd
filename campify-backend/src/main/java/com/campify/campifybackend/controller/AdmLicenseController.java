package com.campify.campifybackend.controller;


import com.campify.campifybackend.dto.LicenseCheckResponse;
import com.campify.campifybackend.model.AdmLicense;
import com.campify.campifybackend.service.AdmLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/licenses")
public class AdmLicenseController {

    private final AdmLicenseService service;

    @Autowired
    public AdmLicenseController(AdmLicenseService service) {
        this.service = service;
    }

    // --- CRUD ENDPOINTS ---

    // 1. CREATE: POST /api/licenses
    @PostMapping
    public ResponseEntity<AdmLicense> createLicense(@RequestBody AdmLicense license) {
        AdmLicense savedLicense = service.save(license);
        return new ResponseEntity<>(savedLicense, HttpStatus.CREATED);
    }

    // 2. READ ALL: GET /api/licenses
    @GetMapping
    public List<AdmLicense> getAllLicenses() {
        return service.findAll();
    }

    // 3. READ SINGLE: GET /api/licenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AdmLicense> getLicenseById(@PathVariable UUID id) {
        return service.findById(id)
                .map(license -> new ResponseEntity<>(license, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 4. UPDATE: PUT /api/licenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AdmLicense> updateLicense(@PathVariable UUID id, @RequestBody AdmLicense licenseDetails) {
        return service.findById(id)
                .map(existingLicense -> {
                    // Update all necessary fields from the request body
                    existingLicense.setActivatedDate(licenseDetails.getActivatedDate());
                    existingLicense.setExpireDate(licenseDetails.getExpireDate());
                    existingLicense.setUserId(licenseDetails.getUserId());
                    existingLicense.setUserNumber(licenseDetails.getUserNumber());
                    existingLicense.setActiveUserNumber(licenseDetails.getActiveUserNumber());
                    existingLicense.setDescription(licenseDetails.getDescription());

                    AdmLicense updatedLicense = service.save(existingLicense);
                    return new ResponseEntity<>(updatedLicense, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 5. DELETE: DELETE /api/licenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicense(@PathVariable UUID id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // --- CUSTOM SERVICE ENDPOINT ---

    /**
     * ADD SERVICE: CheckLicense
     * Endpoint: GET /api/licenses/check/{id}
     */
    @GetMapping("/check/{id}")
    public ResponseEntity<LicenseCheckResponse> checkLicenseStatus(@PathVariable UUID id) {
        LicenseCheckResponse response = service.checkLicense(id);

        // Return 200 OK regardless of validity, allowing the response body to detail the error/success
        if (response.isValid()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // Can be 200 OK or 400 Bad Request, using 200 here to deliver the specific error message as requested
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}