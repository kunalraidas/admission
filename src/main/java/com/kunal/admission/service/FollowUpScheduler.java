package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.VisitorRepository;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class FollowUpScheduler {
    private final VisitorRepository visitorRepository;
    private final EmailService emailService;


    public FollowUpScheduler(VisitorRepository visitorRepository,
                             EmailService emailService){
        this.visitorRepository=visitorRepository;
        this.emailService=emailService;
    }

    //@Scheduled(cron = "0 0 10 * * ?")
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendFollowUpMessages(){
        System.out.println("🔥 FOLLOW-UP SCHEDULER TRIGGERED 🔥");

        LocalDate targetDate = LocalDate.now().minusDays(7);
        List<Visitor> visitors =
                visitorRepository.findByStatusAndSubmissionDateLessThanEqual(
                        AdmissionStatus.NEW,
                        targetDate
                );
        System.out.println("Visitors found : "+ visitors.size());


        for(Visitor visitor : visitors){
            emailService.sendFolloUpEmail(visitor);
            visitor.setStatus(AdmissionStatus.FOLLOW_UP);
            visitorRepository.save(visitor);
        }
    }
}
