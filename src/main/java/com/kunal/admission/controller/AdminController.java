package com.kunal.admission.controller;

import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.service.ApplicationService;
import com.kunal.admission.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ApplicationService applicationService;
    private final VisitorService visitorService;

    public AdminController(ApplicationService applicationService,
                           VisitorService visitorService) {
        this.applicationService = applicationService;
        this.visitorService     = visitorService;
    }

    // ── Visitors ─────────────────────────────────────────────────────────────

    /**
     * GET /admin/visitors
     * Returns all visitors (leads) in the system.
     */
    @GetMapping("/visitors")
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        return ResponseEntity.ok(visitorService.findAll());
    }

    // ── Applications ──────────────────────────────────────────────────────────

    /**
     * GET /admin/applications
     * Returns all student applications.
     */
    @GetMapping("/applications")
    public ResponseEntity<List<StudentApplication>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    /**
     * GET /admin/applications/{id}
     * Returns a single application by ID.
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(applicationService.getApplicationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PUT /admin/applications/{id}/approve
     *
     * Approves the application:
     *   • Sets StudentApplication status → ADMITTED
     *   • Sets Visitor status            → ADMITTED
     *   • Promotes User role             → STUDENT  (new JWT needed after this)
     */
    @PutMapping("/applications/{id}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable Long id) {
        try {
            StudentApplication app = applicationService.approveApplication(id);
            return ResponseEntity.ok(new ActionResponse(
                    "Application approved. Student role has been granted.",
                    app.getId(),
                    app.getStatus().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not approve application. Please try again.");
        }
    }

    /**
     * PUT /admin/applications/{id}/reject
     *
     * Rejects the application:
     *   • Sets StudentApplication status → REJECTED
     *   • Sets Visitor status            → REJECTED
     */
    @PutMapping("/applications/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long id) {
        try {
            StudentApplication app = applicationService.rejectApplication(id);
            return ResponseEntity.ok(new ActionResponse(
                    "Application has been rejected.",
                    app.getId(),
                    app.getStatus().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not reject application. Please try again.");
        }
    }

    // ── Inner response DTO ───────────────────────────────────────────────────

    public record ActionResponse(String message, Long applicationId, String newStatus) {}
}
