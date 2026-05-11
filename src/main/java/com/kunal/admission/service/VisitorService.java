package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.datavalues.Role;
import com.kunal.admission.model.User;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.StudentAppRepository;
import com.kunal.admission.repository.UserRepository;
import com.kunal.admission.repository.VisitorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {
    private final VisitorRepository visitorRepository;
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StudentAppRepository studentAppRepository;

    public VisitorService(VisitorRepository visitorRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          StudentAppRepository studentAppRepository) {
        this.visitorRepository = visitorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.studentAppRepository = studentAppRepository;
    }

    public List<Visitor> findAll() {
        return visitorRepository.findAll();
    }

    public Visitor findById(Long id) {
        Optional<Visitor> visitor = visitorRepository.findById(id);
        return visitor.orElse(null);
    }

    public List<Visitor> searchByEmail(String email) {
        return visitorRepository.findByEmail(email);
    }

    public Visitor save(Visitor visitor) {
        if (visitor.getSubmissionDate() == null) {
            visitor.setSubmissionDate(LocalDate.now());
        }
        if (visitor.getStatus() == null) {
            visitor.setStatus(AdmissionStatus.NEW);
        }
        return visitorRepository.save(visitor);
    }

    public Visitor update(Long id, Visitor visitorDetails) {
        Optional<Visitor> optionalVisitor = visitorRepository.findById(id);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            visitor.setName(visitorDetails.getName());
            visitor.setEmail(visitorDetails.getEmail());
            visitor.setPhone(visitorDetails.getPhone());
            visitor.setCourseInterested(visitorDetails.getCourseInterested());
            if (visitorDetails.getSubmissionDate() != null) {
                visitor.setSubmissionDate(visitorDetails.getSubmissionDate());
            }
            return visitorRepository.save(visitor);
        }
        return null;
    }

    public void delete(Long id) {
        visitorRepository.deleteById(id);
    }

    /**
     * Updates the admission status of a visitor.
     *
     * KEY FEATURE (Requirement 4):
     * If status is set to ADMITTED and a User account doesn't exist yet,
     * the system automatically creates a User with role = STUDENT.
     * A welcome email is also sent.
     *
     * Default password: "Welcome@123" (student should change it after first login)
     */



    public Visitor updateService(Long id,AdmissionStatus status){
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> new RuntimeException("Visitor Not Found"));

        // Requirement 5: profile data must exist before a visitor can be admitted.
        // Block ADMITTED unless a StudentApplication has been submitted for this visitor.
        if (status == AdmissionStatus.ADMITTED && studentAppRepository.findByVisitor(visitor).isEmpty()) {
            throw new RuntimeException(
                    "Cannot mark visitor as ADMITTED: no student application has been submitted yet. " +
                    "The applicant must complete /application/submit first.");
        }

        visitor.setStatus(status);
        visitorRepository.save(visitor);

        // Auto Student Account Creation
        if (status == AdmissionStatus.ADMITTED) {
            boolean alreadyHasAccount = userRepository.existsByEmail(visitor.getEmail());

            if (!alreadyHasAccount) {
                // Default password — student should change this after first login
                String defaultPassword = "Welcome@123";
                String hashedPassword = passwordEncoder.encode(defaultPassword);

                User studentUser = new User(
                        visitor.getName(),
                        visitor.getEmail(),
                        hashedPassword,
                        Role.STUDENT
                );
                userRepository.save(studentUser);

                // Send welcome email with login credentials
                emailService.sendAdmissionConfirmationEmail(visitor, defaultPassword);

                System.out.println("✅ Student account created for: " + visitor.getEmail());
            } else {
                // Account exists (was APPLICANT before) — just upgrade role to STUDENT
                User existingUser = userRepository.findByEmail(visitor.getEmail())
                        .orElseThrow();
                existingUser.setRole(Role.STUDENT);
                userRepository.save(existingUser);

                emailService.sendAdmissionConfirmationEmail(visitor, null);
                System.out.println("✅ Existing user upgraded to STUDENT: " + visitor.getEmail());
            }
        }
        return visitor;
    }
}
