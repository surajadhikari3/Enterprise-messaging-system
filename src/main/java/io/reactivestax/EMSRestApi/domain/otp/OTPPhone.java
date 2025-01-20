package io.reactivestax.EMSRestApi.domain.otp;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OTPPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Client client;
    private String phone;
}
