package io.reactivestax.EMSRestApi.dto.ems;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OtpDTO {
    private Long otpId;
    private Integer otpAttempts;
    private String validOtp;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;
    @Max(value = 5, message = "Max attempt for OTP generation is 5")
    private Integer generationRetryCount;
    @Max(value = 3, message = "Max attempt for OTP validation is 3")
    private Integer validationRetryCount;
    private boolean isValid;
    private String phone;
    @Email(message = "Email must be valid email")
    private String email;
    @NotBlank(message = "Client Id is needed")
    private Long clientId;
}
