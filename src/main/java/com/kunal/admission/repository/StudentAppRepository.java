package com.kunal.admission.repository;

import com.kunal.admission.model.StudentApplication;
import com.kunal.admission.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAppRepository extends JpaRepository<StudentApplication,Long> {
    Optional<StudentApplication> findByVisitor(Visitor visitor);
}
