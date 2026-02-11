package com.kunal.admission.repository;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor,Long> {
    List<Visitor> findByEmail(String email);
    List<Visitor> findByStatusAndSubmissionDateLessThanEqual(AdmissionStatus status, LocalDate submissionDate);

}
