package com.kunal.admission.controller;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitor")
public class VisitorController {
     private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    /**
     * GET /visitor/me
     * Any authenticated user can fetch their own Visitor record (looked up by JWT email).
     * The frontend uses the returned id to call /application/submit/{visitorId}.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();
        List<Visitor> matches = visitorService.searchByEmail(email);
        if (matches.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matches.get(0));
    }

    @GetMapping
    public ResponseEntity<List<Visitor>> getAll() {
        try {
            List<Visitor> visitors = visitorService.findAll();
            return ResponseEntity.ok(visitors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visitor> getById(@PathVariable Long id) {
        try {
            Visitor visitor = visitorService.findById(id);
            if (visitor != null) {
                return ResponseEntity.ok(visitor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Visitor> add( @RequestBody Visitor visitor) {
        try {
            Visitor savedVisitor = visitorService.save(visitor);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVisitor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visitor> update(@PathVariable Long id, @RequestBody Visitor visitor) {
        try {
            Visitor updatedVisitor = visitorService.update(id, visitor);
            if (updatedVisitor != null) {
                return ResponseEntity.ok(updatedVisitor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Visitor> updateStatus(@PathVariable Long id, @RequestParam AdmissionStatus status){
        Visitor updated =  visitorService.updateService(id,status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            visitorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
