package com.kunal.admission.service;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.repository.VisitorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {
    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
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

    public Visitor updateService(Long id,AdmissionStatus status){
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> new RuntimeException("Visitor Not Found"));
        visitor.setStatus(status);
        return visitorRepository.save(visitor);
    }
}
