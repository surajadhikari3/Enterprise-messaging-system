package io.reactivestax.EMSRestApi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.EMSRestApi.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String validOtp;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastAccessed;
    private Integer generationRetryCount = 0;
    private Integer validationRetryCount = 0;

    @Enumerated(EnumType.STRING)
    private Status otpStatus;
    @Enumerated(EnumType.STRING)
    private Status verificationStatus;
    private String phone;
    private String email;

    @OneToOne(mappedBy = "otp", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private Client client;
}
