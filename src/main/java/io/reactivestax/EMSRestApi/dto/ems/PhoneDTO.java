package io.reactivestax.EMSRestApi.dto.ems;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class PhoneDTO {
    private Long id;
    private Long clientId;
    private String outgoingPhoneNumber;
}
