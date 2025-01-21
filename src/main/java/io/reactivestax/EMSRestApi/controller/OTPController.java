package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.dto.OtpDTO;
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
        //and also publish to mq....
    }

    @PostMapping("/call")
    public ResponseEntity<OtpDTO> createOtpForCall(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.createOtpForPhone(otpDTO));
        //and also publish to mq....
    }

    @PostMapping("/email")
    public ResponseEntity<OtpDTO> createOtpForEmail(@Valid @RequestBody OtpDTO otpDTO) {
        return ResponseEntity.ok(otpService.createOtpForEmail(otpDTO));
        //and also publish to mq....
    }

//    @PostMapping("/verify")
//    public ResponseEntity<EmailDTO> createOtpForSms(@Valid @RequestBody EmailDTO emailDTO) {
//        return ResponseEntity.ok(otpService.saveEmail(emailDTO));
//        //and also publish to mq....
//    }
//
//    @PostMapping("/status")
//    public ResponseEntity<EmailDTO> createOtpForSms(@Valid @RequestBody EmailDTO emailDTO) {
//        return ResponseEntity.ok(otpService.saveEmail(emailDTO));
//        //and also publish to mq....
//    }

}
