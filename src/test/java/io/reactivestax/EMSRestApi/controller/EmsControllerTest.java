package io.reactivestax.EMSRestApi.controller;


import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.dto.PhoneDTO;
import io.reactivestax.EMSRestApi.dto.SmsDTO;
import io.reactivestax.EMSRestApi.message.broker.ArtemisProducer;
import io.reactivestax.EMSRestApi.service.EmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmsController.class)
class EmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmsService emsService;

    @MockitoBean
    private ArtemisProducer artemisProducer;


    @Test
    void testCreateEmail() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(1L);
        emailDTO.setClientId(100L);
        emailDTO.setReceiverEmailId("suraj@fullstacklabs.ca");
        emailDTO.setSubject("Welcome");
        emailDTO.setBody("Hello, welcome to Fullstack Labs!");

        when(emsService.saveEmail(any(EmailDTO.class))).thenReturn(emailDTO);

        mockMvc.perform(post("/api/v1/ems/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": 100,
                                    "receiverEmailId": "suraj@fullstacklabs.ca",
                                    "subject": "Welcome",
                                    "body": "Hello, welcome to Fullstack Labs!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value(100))
                .andExpect(jsonPath("$.receiverEmailId").value("suraj@fullstacklabs.ca"))
                .andExpect(jsonPath("$.subject").value("Welcome"))
                .andExpect(jsonPath("$.body").value("Hello, welcome to Fullstack Labs!"));

        verify(emsService).saveEmail(any(EmailDTO.class));
        verify(artemisProducer).sendMessage(anyString(), anyString(), eq("email"));
    }

    @Test
    void testCreateSms() throws Exception {
        // Sample SmsDTO to mock the response
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setId(2L);
        smsDTO.setClientId(101L);
        smsDTO.setPhone("1234567890");
        smsDTO.setMessage("Your OTP is 1234");

        when(emsService.saveSms(any(SmsDTO.class))).thenReturn(smsDTO);

        mockMvc.perform(post("/api/v1/ems/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": 101,
                                    "phone": "1234567890",
                                    "message": "Your OTP is 1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.clientId").value(101))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.message").value("Your OTP is 1234"));

        verify(emsService, atLeastOnce()).saveSms(any(SmsDTO.class));
        verify(artemisProducer).sendMessage(anyString(), anyString(), eq("sms"));
    }

    @Test
    void testCreatePhone() throws Exception {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setId(3L);
        phoneDTO.setClientId(102L);
        phoneDTO.setOutgoingPhoneNumber("9876543210");

        when(emsService.savePhone(any(PhoneDTO.class))).thenReturn(phoneDTO);

        mockMvc.perform(post("/api/v1/ems/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": 102,
                                    "outgoingPhoneNumber": "9876543210"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.clientId").value(102))
                .andExpect(jsonPath("$.outgoingPhoneNumber").value("9876543210"));

        verify(emsService, atLeastOnce()).savePhone(any(PhoneDTO.class));
        verify(artemisProducer).sendMessage(anyString(), anyString(), eq("phone"));
    }
}
