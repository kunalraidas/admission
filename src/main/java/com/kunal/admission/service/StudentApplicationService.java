package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.StudentAppRepository;
import com.kunal.admission.repository.VisitorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentApplicationService {

    private final StudentAppRepository studentAppRepository;
    private final VisitorRepository visitorRepository;
    private final FileStorageService fileStorageService;

    public StudentApplicationService(StudentAppRepository studentAppRepository,
                                     VisitorRepository visitorRepository,
                                     FileStorageService fileStorageService) {
        this.studentAppRepository = studentAppRepository;
        this.visitorRepository = visitorRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Called by an APPLICANT after login.
     * They submit their full application with documents.
     *
     * @param visitorId  - the visitor record linked to this applicant
     * @param application - form fields (name, DOB, qualification, etc.)
     * @param photo      - uploaded photo file
     * @param marksheet  - uploaded marksheet file
     * @param idProof    - uploaded ID proof file
     */
    public StudentApplication submitApplication(Long visitorId,
                                                StudentApplication application,
                                                MultipartFile photo,
                                                MultipartFile marksheet,
                                                MultipartFile idProof) {

        // Find visitor
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found with id: " + visitorId));

        // Check if application already submitted
        if (studentAppRepository.findByVisitor(visitor).isPresent()) {
            throw new RuntimeException("Application already submitted for this visitor.");
        }

        // Save uploaded files to disk, store paths
        if (photo != null && !photo.isEmpty()) {
            application.setPhotoPath(fileStorageService.save(photo, "photo"));
        }
        if (marksheet != null && !marksheet.isEmpty()) {
            application.setMarksheetPath(fileStorageService.save(marksheet, "marksheet"));
        }
        if (idProof != null && !idProof.isEmpty()) {
            application.setIdProofPath(fileStorageService.save(idProof, "idproof"));
        }

        // Link visitor and set metadata
        application.setVisitor(visitor);
        application.setStatus(AdmissionStatus.APPLIED);
        application.setSubmittedAt(LocalDateTime.now());

        // Update visitor status too
        visitor.setStatus(AdmissionStatus.APPLIED);
        visitorRepository.save(visitor);

        return studentAppRepository.save(application);
    }

    /**
     * Get application by visitor ID.
     * An applicant can only see their own application.
     */
    public StudentApplication getByVisitorId(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));

        return studentAppRepository.findByVisitor(visitor)
                .orElseThrow(() -> new RuntimeException("No application found for this visitor"));
    }

    /**
     * Admin: get all applications
     */
    public List<StudentApplication> getAll() {
        return studentAppRepository.findAll();
    }
}