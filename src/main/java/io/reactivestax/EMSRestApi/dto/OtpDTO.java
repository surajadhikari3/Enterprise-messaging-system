package io.reactivestax.EMSRestApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OtpDTO {
    private Long otpId;
    private String validOtp;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;
    @Max(value = 5, message = "Max attempt for OTP generation is 5")
    private Integer generationRetryCount;
    @Max(value = 3, message = "Max attempt for OTP validation is 3")
    private Integer validationRetryCount;
    private Boolean isValid;
    private String phone;
    private String email;
    @NotNull(message = "Client Id is needed")
    private Long clientId;
}
