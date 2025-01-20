package io.reactivestax.EMSRestApi.dto.ems;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OtpDTO {
    private Long otpId;
    private Integer otpAttempts;
    private String validOtp;
    private Date createdAt;
    private Date lastAccessed;
    private Integer generationCount;
    private Integer validationCount;
    private boolean isValid;
    private Long clientId;
}
