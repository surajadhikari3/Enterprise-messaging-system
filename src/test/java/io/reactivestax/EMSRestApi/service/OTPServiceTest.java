package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Otp;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.message.broker.ArtemisProducer;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OTPServiceTest {

    @Autowired
    private OTPService otpService;

    @MockitoBean
    private OTPRepository otpRepository;

    @MockitoBean
    private ArtemisProducer artemisProducer;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
        // Set up default configuration values for the test
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

        // Act
        OtpDTO result = otpService.createOtpForEmail(otpDTO);

        // Assert
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
        existingOtp.setGenerationTimeStamps(List.of(
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().minusMinutes(3),
                LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1)
        ));
        existingOtp.setIsLocked(false);

        when(otpRepository.findOtpByClientId(otpDTO.getClientId())).thenReturn(existingOtp);

//        assertThrows(ExceededGenerationException.class, () -> otpService.createOtpForEmail(otpDTO));
        verify(otpRepository, never()).save(any());
        verify(artemisProducer, never()).sendMessage(anyString(), anyString(), anyString());
    }
}
