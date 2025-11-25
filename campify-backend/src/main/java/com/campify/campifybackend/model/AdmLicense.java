package com.campify.campifybackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "admlicense")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "activateddate")
    private LocalDate activatedDate;

    @Column(name = "expiredate")
    private LocalDate expireDate;

    // Matches the userid column which references public.users
    @Column(name = "userid")
    private UUID userId;

    @Column(name = "usernumber")
    private Integer userNumber;

    @Column(name = "activeusernumber")
    private Integer activeUserNumber;

    @Column(name = "description")
    private String description;
}