package io.reactivestax.EMSRestApi.domain.ems;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.EMSRestApi.dto.ems.ClientDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;
    private Integer otpAttempts;
    private String validOtp;
    private Date createdAt;
    private Date lastAccessed;
    private Integer generationCount;
    private Integer validationCount;
    private boolean isValid;

    @OneToOne(mappedBy = "otp")
    @JsonBackReference
    @ToString.Exclude
    private Client client;
}
