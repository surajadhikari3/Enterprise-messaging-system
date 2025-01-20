package io.reactivestax.EMSRestApi.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Sms {
    private Long id;
    private Long clientId;
    private String phone;
    private String message;
}
