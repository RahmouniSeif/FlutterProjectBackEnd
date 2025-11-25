package com.campify.campifybackend.repository;

import com.campify.campifybackend.model.AdmLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmLicenseRepository extends JpaRepository<AdmLicense, UUID> {
}