package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @PostMapping("/sms")
    public ResponseEntity<OtpDTO> createOtpForSms(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.createOtpForSms(otpDTO));
    }

    @PostMapping("/call")
    public ResponseEntity<OtpDTO> createOtpForCall(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.createOtpForPhone(otpDTO));
    }

    @PostMapping("/email")
    public ResponseEntity<OtpDTO> createOtpForEmail(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.createOtpForEmail(otpDTO));
    }

    @PutMapping("/verify")
    public ResponseEntity<OtpDTO> verifyOtp(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.verifyOtp(otpDTO));
    }

    @GetMapping("/status/{clientId}")
    public ResponseEntity<Status> statusForOTP(@Valid @PathVariable Long clientId) {
        return ResponseEntity.ok(otpService.statusForOTP(clientId));
    }

}
