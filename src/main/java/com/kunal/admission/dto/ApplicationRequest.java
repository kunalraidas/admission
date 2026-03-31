package com.kunal.admission.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ApplicationRequest {
    // ── STEP 1: Personal Information ─────────────────────────────────────────

    private String fullName;           // as per 10th certificate
    private String fatherName;
    private String motherName;
    private LocalDate dob;
    private String gender;             // Male / Female / Other
    private String nationality;
    private String category;           // General / OBC / SC / ST / EWS
    private String aadhaarNumber;      // optional
    private String bloodGroup;
    private String mobileNumber;
    private String alternateContact;   // optional

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

    // 10th
    private String tenthBoard;
    private String tenthSchool;
    private String tenthYearOfPassing;
    private String tenthPercentage;

    // 12th
    private String twelfthBoard;
    private String twelfthSchool;
    private String twelfthStream;
    private String twelfthYearOfPassing;
    private String twelfthPercentage;

    // Graduation (optional)
    private String graduationUniversity;
    private String graduationDegree;
    private String graduationYearOfPassing;
    private String graduationPercentage;

    // ── STEP 3: Course Selection ──────────────────────────────────────────────

    private String campusLocation;

    // ── STEP 4: Declaration ───────────────────────────────────────────────────

    private Boolean declarationAccepted;  // must be true to submit
}
