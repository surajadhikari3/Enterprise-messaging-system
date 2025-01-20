package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.domain.Email;
import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ems/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

   @GetMapping
    public ResponseEntity<List<EmailDTO>> getAllEmails(){
        return ResponseEntity.ok(emailService.findAll());
    }

    @PostMapping
    public ResponseEntity<EmailDTO> createSms(@Valid @RequestBody EmailDTO emailDTO){
       return ResponseEntity.ok(emailService.save(emailDTO));
    }

    public ResponseEntity

}
