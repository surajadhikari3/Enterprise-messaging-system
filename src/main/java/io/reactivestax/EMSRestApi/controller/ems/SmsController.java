package io.reactivestax.EMSRestApi.controller.ems;

import io.reactivestax.EMSRestApi.dto.ems.SmsDTO;
import io.reactivestax.EMSRestApi.service.ems.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ems/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping
    public ResponseEntity<List<SmsDTO>> getAllSms() {
        return ResponseEntity.ok(smsService.findAll());
    }

    @PostMapping
    public ResponseEntity<SmsDTO> createSms(@Valid @RequestBody SmsDTO smsDTO) {
        return ResponseEntity.ok(smsService.save(smsDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SmsDTO> updateSms(@PathVariable Long id, @RequestBody SmsDTO smsDTO) {
        Optional<SmsDTO> sms = smsService.findById(id);
        if (sms.isPresent()) {
            SmsDTO updatedSms = sms.get();
            updatedSms.setPhone(smsDTO.getPhone());
            updatedSms.setClientId(smsDTO.getClientId());
            return ResponseEntity.ok(smsService.save(updatedSms));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSms(@PathVariable Long id) {
        if (smsService.findById(id).isPresent()) {
            smsService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
