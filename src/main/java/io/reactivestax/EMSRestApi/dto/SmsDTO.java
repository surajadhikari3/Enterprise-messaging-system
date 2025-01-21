package io.reactivestax.EMSRestApi.dto;

import jakarta.persistence.Entity;
import lombok.Data;


@Data
public class SmsDTO {
    private Long id;
    private Long clientId;
    private String phone;
    private String message;
}
