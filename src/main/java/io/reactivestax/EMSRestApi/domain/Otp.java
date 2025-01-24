package io.reactivestax.EMSRestApi.domain;

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
    private Boolean isLocked;

    //    @OneToOne(mappedBy = "otp", cascade = CascadeType.ALL)
//    @JsonBackReference
//    @ToString.Exclude
    @Column(name = "client_id")
    private Long clientId;
}
