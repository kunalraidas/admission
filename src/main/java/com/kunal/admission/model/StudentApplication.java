package com.kunal.admission.model;

import com.kunal.admission.datavalues.AdmissionStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class StudentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "id",nullable = false)
    private Visitor visitor;

    // personal info
    private String fatherName;
    private String motherName;
    private LocalDate dob;
    private String address;

    // academic info
    private String lastQualification;
    private String university;
    private Double percentage;

    //documents (paths, not files)
    private String photoPath;
    private String marksheetPath;
    private String idProofPath;

    //

    @Enumerated(EnumType.STRING)
    private AdmissionStatus status;

    private LocalDateTime submittedAt;
}
