package io.reactivestax.EMSRestApi.dto;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class PhoneDTO {
    private Long id;
    private Long clientId;
    private String outgoingPhoneNumber;
}
