package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Otp;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.exception.InvalidOTPException;
import io.reactivestax.EMSRestApi.message.broker.ArtemisProducer;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OTPServiceTest {

    @Autowired
    private OTPService otpService;

    @MockitoBean
    private OTPRepository otpRepository;

    @MockitoBean
    private ArtemisProducer artemisProducer;

    @BeforeEach
    void setUp() {
        otpService.maxOtpGenerationAttempts = 5;
        otpService.slidingWindowHours = 2;
        otpService.queueName = "otp-queue";
    }

    @Test
    void testCreateOtpForEmail_Successful() {
        // Arrange
        OtpDTO otpDTO = OtpDTO.builder()
                .clientId(1001L)
                .email("suraj@fullstacklabs.digital")
                .phone("+1234567890")
                .build();

        Otp existingOtp = new Otp();
        existingOtp.setId(1L);
        existingOtp.setClientId(1001L);
        existingOtp.setEmail("suraj@fullstacklabs.digital");
        existingOtp.setPhone("+1234567890");
        existingOtp.setGenerationTimeStamps(new ArrayList<>());
        existingOtp.setIsLocked(false);

        when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(existingOtp);
        when(otpRepository.save(any(Otp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OtpDTO result = otpService.createOtpForEmail(otpDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(otpDTO.getEmail());
        assertThat(result.getPhone()).isEqualTo(otpDTO.getPhone());
        assertThat(result.getClientId()).isEqualTo(otpDTO.getClientId());
        assertThat(result.getOtpStatus()).isEqualTo(Status.VALID);
        assertThat(result.getVerificationStatus()).isEqualTo(Status.PENDING);
        verify(artemisProducer, times(1)).sendMessage(eq("otp-queue"), anyString(), eq("otp-email"));
    }

    @Test
    void testCreateOtpForEmail_ExceedGenerationAttempts() {
        OtpDTO otpDTO = OtpDTO.builder()
                .clientId(1001L)
                .email("suraj@fullstacklabs.digital")
                .phone("+1234567890")
                .build();

        Otp existingOtp = new Otp();
        existingOtp.setId(1L);
        existingOtp.setClientId(1001L);
        existingOtp.setEmail("suraj@fullstacklabs.digital");
        existingOtp.setPhone("+1234567890");
        existingOtp.setGenerationTimeStamps(List.of(
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().minusMinutes(3),
                LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1)
        ));
        existingOtp.setIsLocked(false);

        when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(existingOtp);
        verify(otpRepository, never()).save(any());
        verify(artemisProducer, never()).sendMessage(anyString(), anyString(), anyString());
    }

    @Test
    void testOtpCreationForFirstTimeGeneration(){
        OtpDTO otpDTO = OtpDTO.builder().clientId(4L).phone("4567345345").email("suraj@fullstack.ca").build();
        Mockito.when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(null);
        OtpDTO otpForSms = otpService.createOtpForSms(otpDTO);
        assertThat(otpForSms.getValidOtp()).isNotNull();
        assertThat(otpForSms.getClientId()).isEqualTo(4L);
    }


    @Test
    void testVerifyOtp_NullOtp_ShouldThrowInvalidOTPException() {
        OtpDTO otpDTO = OtpDTO.builder().clientId(1L).build();
        Mockito.when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(null);
        assertThrows(InvalidOTPException.class, () -> otpService.verifyOtp(otpDTO));
    }

    @Test
    void testVerifyOtp_InvalidOtp_ShouldIncrementRetryAndThrow() {
        Otp otp = new Otp();
        otp.setOtpStatus(Status.INVALID);
        otp.setValidationRetryCount(0);
        OtpDTO otpDTO = OtpDTO.builder().clientId(1L).validOtp("1234").build();

        Mockito.when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(otp);

        assertThrows(InvalidOTPException.class, () -> otpService.verifyOtp(otpDTO));
        Mockito.verify(otpRepository).save(otp);
        assertEquals(1, otp.getValidationRetryCount());
    }

    @Test
    void testVerifyOtp_ValidOtp_ShouldSetVerifiedStatus() {
        Otp otp = new Otp();
        otp.setOtpStatus(Status.VALID);
        otp.setValidOtp("1234");
        otp.setValidationRetryCount(1);
        OtpDTO otpDTO = OtpDTO.builder().clientId(1L).validOtp("1234").build();

        Mockito.when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(otp);

        otpService.verifyOtp(otpDTO);

        assertEquals(Status.VERIFIED, otp.getVerificationStatus());
        assertEquals(0, otp.getValidationRetryCount());
        Mockito.verify(otpRepository).save(otp);
    }

    @Test
    void testStatusForOTP_ExpiredOtp_ShouldReturnExpiredStatus() {
        Otp otp = new Otp();
        otp.setGenerationTimeStamps(List.of(LocalDateTime.now().minusMinutes(3)));
        otp.setValidationRetryCount(0);
        Mockito.when(otpRepository.findOtpByClientId(1L)).thenReturn(otp);

        Status result = otpService.statusForOTP(1L);
        assertEquals(Status.EXPIRED, result);
    }

    @Test
    void testStatusForOTP_ValidOtp_ShouldReturnValidStatus() {
        Otp otp = new Otp();
        otp.setGenerationTimeStamps(List.of(LocalDateTime.now()));
        otp.setValidationRetryCount(0);
        Mockito.when(otpRepository.findOtpByClientId(1L)).thenReturn(otp);
        Status result = otpService.statusForOTP(1L);
        assertEquals(Status.VALID, result);
    }
}
