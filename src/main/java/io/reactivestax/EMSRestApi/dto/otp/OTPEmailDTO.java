package io.reactivestax.EMSRestApi.dto.otp;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class OTPEmailDTO {
    private Long id;
    private Long clientId;
    private String email;
}
