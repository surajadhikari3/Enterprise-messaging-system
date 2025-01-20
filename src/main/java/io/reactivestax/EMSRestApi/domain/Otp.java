package io.reactivestax.EMSRestApi.domain;

import io.reactivestax.EMSRestApi.dto.ClientDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;
    private Integer otpAttempts;
    private Date createdAt;
    private Date lastAccessed;
    private Integer generationCount;
    private Integer validationCount;
    private boolean isLocked;
    private ClientDTO clientDTO;
}
