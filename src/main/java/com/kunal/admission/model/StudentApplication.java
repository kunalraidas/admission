package com.kunal.admission.model;

import com.kunal.admission.datavalues.AdmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_application")
public class StudentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visitor_id",nullable = false)
    private Visitor visitor;

    // ── STEP 1: Personal Information ─────────────────────────────────────────

    private String fullName;          // as per 10th certificate
    private String fatherName;
    private String motherName;
    private LocalDate dob;
    private String gender;            // Male / Female / Other
    private String nationality;
    private String category;          // General / OBC / SC / ST / EWS
    private String aadhaarNumber;     // optional
    private String bloodGroup;
    private String mobileNumber;
    private String alternateContact;

    // Permanent Address
    private String permAddressLine1;
    private String permAddressLine2;
    private String permCity;
    private String permState;
    private String permPincode;

    // Correspondence Address (if different from permanent)
    private String corrAddressLine1;
    private String corrAddressLine2;
    private String corrCity;
    private String corrState;
    private String corrPincode;

    // ── STEP 2: Academic Information ──────────────────────────────────────────

    // 10th Details
    private String tenthBoard;
    private String tenthSchool;
    private String tenthYearOfPassing;
    private String tenthPercentage;
    private String tenthMarksheetPath;    // file path after upload

    // 12th Details
    private String twelfthBoard;
    private String twelfthSchool;
    private String twelfthStream;         // Science / Commerce / Arts
    private String twelfthYearOfPassing;
    private String twelfthPercentage;
    private String twelfthMarksheetPath;  // file path after upload

    // Graduation (optional)
    private String graduationUniversity;
    private String graduationDegree;
    private String graduationYearOfPassing;
    private String graduationPercentage;
    private String graduationMarksheetPath; // file path after upload

    // ── STEP 3: Course Selection ──────────────────────────────────────────────

    // course is already in Visitor.courseInterested — only campus is new
    private String campusLocation;

    // ── STEP 4: Declaration ───────────────────────────────────────────────────

    private Boolean declarationAccepted;      // true = checkbox ticked
    private String  signaturePath;            // uploaded digital signature path

    // ── Documents (photo + ID proof from DocumentController) ─────────────────

    private String photoPath;
    private String idProofPath;

    // ── Status & Timestamps ───────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    private AdmissionStatus status;

    private LocalDateTime submittedAt;
}
