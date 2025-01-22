package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/twilio/sms")
public class TwilioController {

    @Autowired
    private TwilioService twilioService;

    @PostMapping("/send")
    public String sendSms(@RequestParam String to, @RequestParam String message) {
        return twilioService.sendSms(to, message);
    }
}
