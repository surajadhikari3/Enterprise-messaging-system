package io.reactivestax.EMSRestApi.dto.otp;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class OTPPhoneDTO {

    private Long id;
    private Long clientId;
    private String phone;
}
