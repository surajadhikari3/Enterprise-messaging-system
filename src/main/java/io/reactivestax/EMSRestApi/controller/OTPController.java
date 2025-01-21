package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
public class OTPController {

    @Autowired
    private OTPService otpService;

}
