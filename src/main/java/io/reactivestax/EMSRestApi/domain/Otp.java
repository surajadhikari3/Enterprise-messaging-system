package io.reactivestax.EMSRestApi.domain;

import io.reactivestax.EMSRestApi.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private List<LocalDateTime> generationTimeStamps = new ArrayList<>();
    private Integer validationRetryCount = 0;
    @Enumerated(EnumType.STRING)
    private Status otpStatus;

    @Enumerated(EnumType.STRING)
    private Status verificationStatus;
    private String phone;
    private String email;
    private Boolean isLocked;

    @Column(name = "client_id")
    private Long clientId;
}
