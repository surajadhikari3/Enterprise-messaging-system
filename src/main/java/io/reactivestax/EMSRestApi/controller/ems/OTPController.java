package io.reactivestax.EMSRestApi.controller.ems;

import io.reactivestax.EMSRestApi.dto.ems.EmailDTO;
import io.reactivestax.EMSRestApi.service.ems.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/otp")
public class OTPController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<EmailDTO>> getAllEmails() {
        return ResponseEntity.ok(emailService.findAll());
    }

    @PostMapping
    public ResponseEntity<EmailDTO> createEmail(@Valid @RequestBody EmailDTO emailDTO) {
        return ResponseEntity.ok(emailService.save(emailDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailDTO> updateEmail(@PathVariable Long id, @RequestBody EmailDTO emailDTO) {
        Optional<EmailDTO> email = emailService.findById(id);
        if (email.isPresent()) {
            EmailDTO updatedEmail = email.get();
            updatedEmail.setTo(emailDTO.getTo());
            updatedEmail.setBody(emailDTO.getBody());
            updatedEmail.setSubject(emailDTO.getSubject());
            updatedEmail.setClientId(emailDTO.getClientId());
            return ResponseEntity.ok(emailService.save(updatedEmail));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        if (emailService.findById(id).isPresent()) {
            emailService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
