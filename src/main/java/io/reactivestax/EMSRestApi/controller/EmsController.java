package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.dto.PhoneDTO;
import io.reactivestax.EMSRestApi.dto.SmsDTO;
import io.reactivestax.EMSRestApi.message.broker.ArtemisProducer;
import io.reactivestax.EMSRestApi.service.EmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ems")

public class EmsController {

    @Autowired
    private EmsService emsService;

    @Autowired
    private ArtemisProducer artemisProducer;

    String QUEUE_NAME = "ems-queue";

    @PostMapping("/email")
    public ResponseEntity<EmailDTO> createEmail(@Valid @RequestBody EmailDTO emailDTO) {
        EmailDTO savedDTO = emsService.saveEmail(emailDTO);
        artemisProducer.sendMessage(QUEUE_NAME, savedDTO.getId().toString(), "email");
        return ResponseEntity.ok(savedDTO);
    }

    @PostMapping("/sms")
    public ResponseEntity<SmsDTO> createSms(@Valid @RequestBody SmsDTO smsDTO) {
        SmsDTO savedDTO = emsService.saveSms(smsDTO);
        artemisProducer.sendMessage(QUEUE_NAME, savedDTO.getId().toString(), "sms");
        return ResponseEntity.ok(emsService.saveSms(smsDTO));
    }

    @PostMapping("/phone")
    public ResponseEntity<PhoneDTO> createPhone(@Valid @RequestBody PhoneDTO phoneDto) {
        PhoneDTO savedDTO = emsService.savePhone(phoneDto);
        artemisProducer.sendMessage(QUEUE_NAME, savedDTO.getId().toString(), "phone");
        return ResponseEntity.ok(emsService.savePhone(phoneDto));
    }
}
