package com.kunal.admission.service;

import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.VisitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class EmailService {
   // private final VisitorRepository visitorRepository;
    //private static final Logger log = (Logger) LoggerFactory.getLogger(FollowUpScheduler.class);
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    public void sendFolloUpEmail(Visitor visitor){
        log.info("Follow-up email sent to {}", visitor.getEmail());
        log.info("Hello {}, place update your admission status.", visitor.getName());

       // System.out.println("Follow Up From Bvicam: "+visitor.getEmail());
       // System.out.println("Hello "+ visitor.getName()+ ", place update you admission Status. ");
//        log.info("Follow-ip sent to {}");
//        log.info(visitor.getEmail());
    }
}
