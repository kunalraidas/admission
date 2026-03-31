package com.kunal.admission.controller;

import com.kunal.admission.dto.ApplicationRequest;
import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    /**
     * POST /application/submit
     * Role: APPLICANT
     *
     * Submit a new admission application.
     * The JWT token identifies the applicant — no need to pass email in body.
     *
     * Request body:
     * {
     *   "fatherName": "Ram Kumar",
     *   "motherName": "Sita Kumar",
     *   "dob": "2000-05-15",
     *   "address": "123, Main Street, Delhi",
     *   "lastQualification": "BCA",
     *   "university": "Delhi University",
     *   "percentage": 78.5
     * }
     */

    @PostMapping("/submit")
    public ResponseEntity<?> submitApplication(
            Authentication auth,
            @RequestBody ApplicationRequest request){
        try{
            String email = auth.getName(); // extracted from JWT by Spring Security
            StudentApplication app = applicationService.submit(email, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(app);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }

    /**
     * GET /application/me
     * Role: APPLICANT, STUDENT, ADMIN
     *
     * Returns the authenticated user's own application.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyApplication(Authentication auth){
        try {
            String email = auth.getName();
            StudentApplication app = applicationService.getMyApplication(email);
            return ResponseEntity.ok(app);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }
}
