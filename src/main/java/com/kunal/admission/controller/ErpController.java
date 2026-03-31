package com.kunal.admission.controller;

import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/erp")
@PreAuthorize("hasRole('STUDENT')")
public class ErpController {
    private final ApplicationService applicationService;

    public ErpController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * GET /erp/dashboard
     * Role: STUDENT (admitted students only)
     *
     * Returns the admitted student's application details and a welcome message.
     *
     * Response:
     * {
     *   "welcome": "Welcome, Ravi Kumar! You are admitted to MCA.",
     *   "application": { ... }
     * }
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Authentication auth) {
        try {
            String email = auth.getName();
            StudentApplication app = applicationService.getMyApplication(email);

            String studentName = app.getVisitor().getName();
            String course      = app.getVisitor().getCourseInterested();
            String welcome     = "Welcome, " + studentName + "! You are admitted to " + course + ".";

            return ResponseEntity.ok(Map.of(
                    "welcome",     welcome,
                    "application", app
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }
}
