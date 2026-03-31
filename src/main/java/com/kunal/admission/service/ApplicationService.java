package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.datavalues.Role;
import com.kunal.admission.dto.ApplicationRequest;
import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.StudentAppRepository;
import com.kunal.admission.repository.UserRepository;
import com.kunal.admission.repository.VisitorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final StudentAppRepository appRepository;
    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;

    public ApplicationService(StudentAppRepository appRepository,
                              VisitorRepository visitorRepository,
                              UserRepository userRepository) {
        this.appRepository     = appRepository;
        this.visitorRepository = visitorRepository;
        this.userRepository    = userRepository;
    }

    // ── APPLICANT: Submit full 4-step application ─────────────────────────────

    public StudentApplication submit(String email, ApplicationRequest req) {

        // Guard: declaration must be accepted
        if (req.getDeclarationAccepted() == null || !req.getDeclarationAccepted()) {
            throw new RuntimeException("You must accept the declaration before submitting.");
        }

        // Find visitor tied to this user's email
        List<Visitor> visitors = visitorRepository.findByEmail(email);
        if (visitors.isEmpty()) {
            throw new RuntimeException("No visitor record found for your account. Please contact admissions.");
        }
        Visitor visitor = visitors.get(0);

        // Block duplicate submissions
        appRepository.findByVisitor(visitor).ifPresent(existing -> {
            throw new RuntimeException("You have already submitted an application (ID: " + existing.getId() + ").");
        });

        StudentApplication app = new StudentApplication();
        app.setVisitor(visitor);

        // ── Step 1: Personal Information ──────────────────────────────────────
        app.setFullName(req.getFullName());
        app.setFatherName(req.getFatherName());
        app.setMotherName(req.getMotherName());
        app.setDob(req.getDob());
        app.setGender(req.getGender());
        app.setNationality(req.getNationality());
        app.setCategory(req.getCategory());
        app.setAadhaarNumber(req.getAadhaarNumber());
        app.setBloodGroup(req.getBloodGroup());
        app.setMobileNumber(req.getMobileNumber());
        app.setAlternateContact(req.getAlternateContact());

        // Permanent Address
        app.setPermAddressLine1(req.getPermAddressLine1());
        app.setPermAddressLine2(req.getPermAddressLine2());
        app.setPermCity(req.getPermCity());
        app.setPermState(req.getPermState());
        app.setPermPincode(req.getPermPincode());

        // Correspondence Address
        app.setCorrAddressLine1(req.getCorrAddressLine1());
        app.setCorrAddressLine2(req.getCorrAddressLine2());
        app.setCorrCity(req.getCorrCity());
        app.setCorrState(req.getCorrState());
        app.setCorrPincode(req.getCorrPincode());

        // ── Step 2: Academic Information ──────────────────────────────────────
        app.setTenthBoard(req.getTenthBoard());
        app.setTenthSchool(req.getTenthSchool());
        app.setTenthYearOfPassing(req.getTenthYearOfPassing());
        app.setTenthPercentage(req.getTenthPercentage());

        app.setTwelfthBoard(req.getTwelfthBoard());
        app.setTwelfthSchool(req.getTwelfthSchool());
        app.setTwelfthStream(req.getTwelfthStream());
        app.setTwelfthYearOfPassing(req.getTwelfthYearOfPassing());
        app.setTwelfthPercentage(req.getTwelfthPercentage());

        app.setGraduationUniversity(req.getGraduationUniversity());
        app.setGraduationDegree(req.getGraduationDegree());
        app.setGraduationYearOfPassing(req.getGraduationYearOfPassing());
        app.setGraduationPercentage(req.getGraduationPercentage());

        // ── Step 3: Course Selection ───────────────────────────────────────────
        app.setCampusLocation(req.getCampusLocation());

        // ── Step 4: Declaration ────────────────────────────────────────────────
        app.setDeclarationAccepted(req.getDeclarationAccepted());

        app.setStatus(AdmissionStatus.APPLIED);
        app.setSubmittedAt(LocalDateTime.now());

        // Sync visitor status
        visitor.setStatus(AdmissionStatus.APPLIED);
        visitorRepository.save(visitor);

        return appRepository.save(app);
    }

    // ── APPLICANT / STUDENT: View own application ─────────────────────────────

    public StudentApplication getMyApplication(String email) {
        List<Visitor> visitors = visitorRepository.findByEmail(email);
        if (visitors.isEmpty()) {
            throw new RuntimeException("No visitor record found for your account.");
        }
        return appRepository.findByVisitor(visitors.get(0))
                .orElseThrow(() -> new RuntimeException("You have not submitted an application yet."));
    }

    // ── ADMIN: View all applications ──────────────────────────────────────────

    public List<StudentApplication> getAllApplications() {
        return appRepository.findAll();
    }

    public StudentApplication getApplicationById(Long id) {
        return appRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + id));
    }

    // ── ADMIN: Approve → ADMITTED + promote role to STUDENT ──────────────────

    public StudentApplication approveApplication(Long id) {
        StudentApplication app = getApplicationById(id);
        app.setStatus(AdmissionStatus.ADMITTED);
        appRepository.save(app);

        Visitor visitor = app.getVisitor();
        visitor.setStatus(AdmissionStatus.ADMITTED);
        visitorRepository.save(visitor);

        // Promote APPLICANT → STUDENT
        userRepository.findByEmail(visitor.getEmail()).ifPresent(user -> {
            user.setRole(Role.STUDENT);
            userRepository.save(user);
        });

        return app;
    }

    // ── ADMIN: Reject ─────────────────────────────────────────────────────────

    public StudentApplication rejectApplication(Long id) {
        StudentApplication app = getApplicationById(id);
        app.setStatus(AdmissionStatus.REJECTED);
        appRepository.save(app);

        Visitor visitor = app.getVisitor();
        visitor.setStatus(AdmissionStatus.REJECTED);
        visitorRepository.save(visitor);

        return app;
    }

    // ── Document path updaters (called by DocumentController) ─────────────────

    public StudentApplication updatePhotoPath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setPhotoPath(path);
        return appRepository.save(app);
    }

    public StudentApplication updateIdProofPath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setIdProofPath(path);
        return appRepository.save(app);
    }

    public StudentApplication updateTenthMarksheetPath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setTenthMarksheetPath(path);
        return appRepository.save(app);
    }

    public StudentApplication updateTwelfthMarksheetPath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setTwelfthMarksheetPath(path);
        return appRepository.save(app);
    }

    public StudentApplication updateGraduationMarksheetPath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setGraduationMarksheetPath(path);
        return appRepository.save(app);
    }

    public StudentApplication updateSignaturePath(String email, String path) {
        StudentApplication app = getMyApplication(email);
        app.setSignaturePath(path);
        return appRepository.save(app);
    }
}