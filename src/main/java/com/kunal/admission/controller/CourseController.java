package com.kunal.admission.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {
    public ResponseEntity<List<Map<String,String>>> getAllCourses(){
        Map<String, String> mca = Map.of(
                "code",        "MCA",
                "name",        "Master of Computer Applications",
                "duration",    "2 Years (4 Semesters)",
                "eligibility", "Bachelor's degree with Mathematics / Statistics / Computer Science",
                "seats",       "60",
                "description", "MCA is a postgraduate programme focused on advanced computing, software development, and IT management."
        );

        Map<String, String> baJmc = Map.of(
                "code",        "BA_JMC",
                "name",        "Bachelor of Arts in Journalism and Mass Communication",
                "duration",    "3 Years (6 Semesters)",
                "eligibility", "10+2 (any stream) from a recognized board",
                "seats",       "40",
                "description", "BA(JMC) covers print journalism, broadcast media, digital communication, and public relations."
        );

        return ResponseEntity.ok(List.of(mca,baJmc));
    }
}
