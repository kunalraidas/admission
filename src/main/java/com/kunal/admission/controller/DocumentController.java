package com.kunal.admission.controller;

import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.service.ApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final ApplicationService applicationService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public DocumentController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private static final long MAX_BYTES = 5 * 1024 * 1024L; // 5 MB

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file provided.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new RuntimeException("File too large. Maximum allowed size is 5 MB.");
        }
        String ct = file.getContentType();
        if (ct == null || (!ct.equals("image/jpeg") && !ct.equals("image/png") && !ct.equals("application/pdf"))) {
            throw new RuntimeException("Only JPEG, PNG, and PDF files are allowed.");
        }
    }

    /** Saves to uploadDir/<subfolder>/<UUID>_<originalName> and returns the full path. */
    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        Path dir = Paths.get(uploadDir, subfolder);
        Files.createDirectories(dir);
        String name = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dest = dir.resolve(name);
        file.transferTo(dest.toFile());
        return dest.toString();
    }
    // ── Generic upload helper to cut boilerplate ──────────────────────────────

    @FunctionalInterface
    interface PathUpdater {
        StudentApplication update(String email, String path);
    }

    private ResponseEntity<?> handleUpload(MultipartFile file,
                                           String subfolder,
                                           Authentication auth,
                                           PathUpdater updater) {
        try {
            validateFile(file);
            String path = saveFile(file, subfolder);
            StudentApplication app = updater.update(auth.getName(), path);
            return ResponseEntity.ok(new UploadResponse("File uploaded successfully.", path, app.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File could not be saved. Please try again.");
        }
    }

    // ── Upload endpoints ──────────────────────────────────────────────────────

    /**
     * POST /document/photo
     * Passport-size photograph.
     */
    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file,
                                         Authentication auth) {
        return handleUpload(file, "photos", auth, applicationService::updatePhotoPath);
    }

    /**
     * POST /document/idproof
     * Government ID proof (Aadhaar / PAN / Passport etc.)
     */
    @PostMapping("/idproof")
    public ResponseEntity<?> uploadIdProof(@RequestParam("file") MultipartFile file,
                                           Authentication auth) {
        return handleUpload(file, "idproofs", auth, applicationService::updateIdProofPath);
    }

    /**
     * POST /document/marksheet/10th
     * 10th standard marksheet — uploaded on Step 2 of the application form.
     */
    @PostMapping("/marksheet/10th")
    public ResponseEntity<?> upload10thMarksheet(@RequestParam("file") MultipartFile file,
                                                 Authentication auth) {
        return handleUpload(file, "marksheets/10th", auth, applicationService::updateTenthMarksheetPath);
    }

    /**
     * POST /document/marksheet/12th
     * 12th standard marksheet — uploaded on Step 2 of the application form.
     */
    @PostMapping("/marksheet/12th")
    public ResponseEntity<?> upload12thMarksheet(@RequestParam("file") MultipartFile file,
                                                 Authentication auth) {
        return handleUpload(file, "marksheets/12th", auth, applicationService::updateTwelfthMarksheetPath);
    }

    /**
     * POST /document/marksheet/graduation
     * Graduation marksheet (optional) — uploaded on Step 2 of the application form.
     */
    @PostMapping("/marksheet/graduation")
    public ResponseEntity<?> uploadGraduationMarksheet(@RequestParam("file") MultipartFile file,
                                                       Authentication auth) {
        return handleUpload(file, "marksheets/graduation", auth, applicationService::updateGraduationMarksheetPath);
    }

    /**
     * POST /document/signature
     * Digital signature image — uploaded on Step 4 (Declaration).
     */
    @PostMapping("/signature")
    public ResponseEntity<?> uploadSignature(@RequestParam("file") MultipartFile file,
                                             Authentication auth) {
        return handleUpload(file, "signatures", auth, applicationService::updateSignaturePath);
    }

    // ── Response DTO ──────────────────────────────────────────────────────────

    public record UploadResponse(String message, String storedPath, Long applicationId) {}
}
