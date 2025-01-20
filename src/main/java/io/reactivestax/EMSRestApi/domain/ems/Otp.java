package io.reactivestax.EMSRestApi.domain.ems;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;
    private Integer otpAttempts;
    private String validOtp;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastAccessed;
    private Integer generationRetryCount = 0;
    private Integer validationRetryCount = 0;
    private Boolean isValid;
    private String phone;
    private String email;

    @OneToOne(mappedBy = "otp", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private Client client;
}
