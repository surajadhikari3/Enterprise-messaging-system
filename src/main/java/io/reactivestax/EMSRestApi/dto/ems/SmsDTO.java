package io.reactivestax.EMSRestApi.dto.ems;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class SmsDTO {
    private Long id;
    private Long clientId;
    private String phone;
    private String message;
}
