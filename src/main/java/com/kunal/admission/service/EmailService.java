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
                        "Please update your admission status at the earliest.\n\n" +
                        "Regards,\n" +
                        "BVICAM Admission Team"
        );
        mailSender.send(message);
        log.info("Follow - Up email sent to {}", visitor.getEmail());
    }
}
