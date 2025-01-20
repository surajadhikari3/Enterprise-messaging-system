package io.reactivestax.EMSRestApi.controller.ems;

import io.reactivestax.EMSRestApi.dto.ems.PhoneDTO;
import io.reactivestax.EMSRestApi.service.ems.PhoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ems/phone")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @GetMapping
    public ResponseEntity<List<PhoneDTO>> getAllPhone() {
        return ResponseEntity.ok(phoneService.findAll());
    }

    @PostMapping
    public ResponseEntity<PhoneDTO> createPhone(@Valid @RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(phoneService.save(phoneDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneDTO> updatePhone(@PathVariable Long id, @RequestBody PhoneDTO phoneDTO) {
        Optional<PhoneDTO> phone = phoneService.findById(id);
        if (phone.isPresent()) {
            PhoneDTO updatedPhone = phone.get();
            updatedPhone.setOutgoingPhoneNumber(phoneDTO.getOutgoingPhoneNumber());
            updatedPhone.setClientId(phoneDTO.getClientId());
            return ResponseEntity.ok(phoneService.save(updatedPhone));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        if (phoneService.findById(id).isPresent()) {
            phoneService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
