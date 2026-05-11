package com.kunal.admission.service;

import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.VisitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFolloUpEmail(Visitor visitor){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(visitor.getEmail());
        message.setSubject("Admission Follow-up | BVICAM");
        message.setText(
                "Hello " + visitor.getName() + ",\n\n" +
                        "We noticed that you started your admission process.\n" +
                        "Thank you for your interest in BVICAM!\n\n" +
                        "Here are the courses we offer:\n\n" +
                        "1. BCA (Bachelor of Computer Applications) - 3 Years\n" +
                        "2. MCA (Master of Computer Applications) - 2 Years\n" +
                        "3. B.Tech (Computer Science & Engineering) - 4 Years\n" +
                        "4. MBA (Master of Business Administration) - 2 Years\n\n" +
                        "For admission details, fee structure, and eligibility,\n" +
                        "please visit: https://www.bvicam.ac.in\n\n" +
                        "Or contact our admission office at: admissions@bvicam.ac.in\n\n" +
                        "Please update your admission status at the earliest.\n\n" +
                        "Regards,\n" +
                        "BVICAM Admission Team"

        );
        mailSender.send(message);
        log.info("Follow - Up email sent to {}", visitor.getEmail());
    }

    /**
     * Sent automatically when Admin sets a visitor's status to ADMITTED.
     * (Requirement 4 — Student Account Creation)
     *
     * @param defaultPassword  null if user already had an account (APPLICANT → STUDENT upgrade)
     */
    public void sendAdmissionConfirmationEmail(Visitor visitor, String defaultPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(visitor.getEmail());
        message.setSubject("Congratulations! You've been Admitted | BVICAM");

        String body;
        if (defaultPassword != null) {
            // New student — first time account
            body = "Dear " + visitor.getName() + ",\n\n" +
                    "Congratulations! Your admission to BVICAM has been confirmed.\n\n" +
                    "Your ERP login credentials are:\n" +
                    "  Email:    " + visitor.getEmail() + "\n" +
                    "  Password: " + defaultPassword + "\n\n" +
                    "Please login and change your password immediately.\n\n" +
                    "Regards,\n" +
                    "BVICAM Admission Team";
        } else {
            // Existing applicant account upgraded to STUDENT
            body = "Dear " + visitor.getName() + ",\n\n" +
                    "Congratulations! Your admission to BVICAM has been confirmed.\n" +
                    "Your existing account has been upgraded to a Student account.\n" +
                    "Login with your registered email and password.\n\n" +
                    "Regards,\n" +
                    "BVICAM Admission Team";
        }

        message.setText(body);
        mailSender.send(message);
        log.info("Admission confirmation email sent to {}", visitor.getEmail());
    }
}
