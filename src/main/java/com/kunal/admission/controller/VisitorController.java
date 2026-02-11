package com.kunal.admission.controller;

import com.kunal.admission.datavalues.AdmissionStatus;
import com.kunal.admission.model.Visitor;
import com.kunal.admission.service.VisitorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitor")
public class VisitorController {
    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
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
