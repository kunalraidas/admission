package com.kunal.admission.controller;

import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.service.StudentApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/application")
public class StudentApplicationController {

    private final StudentApplicationService applicationService;

    public StudentApplicationController(StudentApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * POST /application/submit/{visitorId}
     * Role: APPLICANT (or ADMIN)
     *
     * Submit a full application with documents.
     * Use multipart/form-data in Postman.
     *
     * Form fields:
     *   fatherName, motherName, dob (YYYY-MM-DD),
     *   address, lastQualification, university, percentage
     *
     * Files:
     *   photo, marksheet, idProof
     */
    @PostMapping(value = "/submit/{visitorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('APPLICANT','ADMIN')")
    public ResponseEntity<?> submitApplication(
            @PathVariable Long visitorId,

            // Form fields mapped manually (easier than @ModelAttribute with files)
            @RequestParam String fatherName,
            @RequestParam String motherName,
            @RequestParam String dob,          // "1999-05-20"
            @RequestParam String address,
            @RequestParam String lastQualification,
            @RequestParam String university,
            @RequestParam Double percentage,

            // File uploads (optional — can be null if not provided)
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile marksheet,
            @RequestParam(required = false) MultipartFile idProof
    ) {
        try {
            // Build the application object from form params
            StudentApplication application = new StudentApplication();
            application.setFatherName(fatherName);
            application.setMotherName(motherName);
            application.setDob(LocalDate.parse(dob));
            application.setAddress(address);
            application.setLastQualification(lastQualification);
            application.setUniversity(university);
            application.setPercentage(percentage);

            StudentApplication saved = applicationService.submitApplication(
                    visitorId, application, photo, marksheet, idProof
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    /**
     * GET /application/{visitorId}
     * Role: APPLICANT, STUDENT, ADMIN
     *
     * Fetch application details for a visitor.
     */
    @GetMapping("/{visitorId}")
    @PreAuthorize("hasAnyRole('APPLICANT','STUDENT','ADMIN')")
    public ResponseEntity<?> getApplication(@PathVariable Long visitorId) {
        try {
            StudentApplication application = applicationService.getByVisitorId(visitorId);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * GET /application/all
     * Role: ADMIN only
     *
     * List all submitted applications.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentApplication>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAll());
    }
}