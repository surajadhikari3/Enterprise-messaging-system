package io.reactivestax.EMSRestApi.controller;

import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.service.OTPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OTPController.class)
class OTPControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OTPService otpService;

    private OtpDTO mockOtpDTO;

    @BeforeEach
    void setUp() {
        mockOtpDTO = OtpDTO.builder()
                .clientId(1L)
                .phone("1234567890")
                .email("suraj@fullStacklabs.ca") // Updated email here
                .validOtp("123456")
                .build();
    }

    @Test
    void testCreateOtpForSms() throws Exception {
        when(otpService.createOtpForSms(any(OtpDTO.class))).thenReturn(mockOtpDTO);

        mockMvc.perform(post("/api/v1/otp/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "phone": "1234567890",
                                  "email": "suraj@fullStacklabs.ca"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("suraj@fullStacklabs.ca"))
                .andExpect(jsonPath("$.validOtp").value("123456"));
    }

    @Test
    void testCreateOtpForCall() throws Exception {
        when(otpService.createOtpForPhone(any(OtpDTO.class))).thenReturn(mockOtpDTO);

        mockMvc.perform(post("/api/v1/otp/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "phone": "1234567890",
                                  "email": "suraj@fullStacklabs.ca"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("suraj@fullStacklabs.ca"))
                .andExpect(jsonPath("$.validOtp").value("123456"));
    }

    @Test
    void testCreateOtpForEmail() throws Exception {
        when(otpService.createOtpForEmail(any(OtpDTO.class))).thenReturn(mockOtpDTO);

        mockMvc.perform(post("/api/v1/otp/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "phone": "1234567890",
                                  "email": "suraj@fullStacklabs.ca"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("suraj@fullStacklabs.ca"))
                .andExpect(jsonPath("$.validOtp").value("123456"));
    }

    @Test
    void testVerifyOtp() throws Exception {
        when(otpService.verifyOtp(any(OtpDTO.class))).thenReturn(mockOtpDTO);

        mockMvc.perform(put("/api/v1/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "validOtp": "123456",
                                  "phone": "1234567890",
                                  "email": "suraj@fullStacklabs.ca"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.validOtp").value("123456"));
    }

    @Test
    void testStatusForOtp() throws Exception {
        when(otpService.statusForOTP(1L)).thenReturn(Status.VALID);

        mockMvc.perform(get("/api/v1/otp/status/1"))
                .andExpect(status().isOk());

    }
}
