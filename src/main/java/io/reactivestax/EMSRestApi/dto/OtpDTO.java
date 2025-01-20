package io.reactivestax.EMSRestApi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OtpDTO {
    private Long otpId;
    private Integer otpAttempts;
    private Date createdAt;
    private Date lastAccessed;
    private Integer generationCount;
    private Integer validationCount;
    private Long clientId;
}
