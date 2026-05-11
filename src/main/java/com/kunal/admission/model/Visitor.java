package com.kunal.admission.model;

import com.kunal.admission.datavalues.AdmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "visitor")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String phone;

    @NonNull
    @Column(nullable = false)
    private String courseInterested;

    private LocalDate submissionDate;

    @Enumerated(EnumType.STRING)
    private AdmissionStatus status;
}