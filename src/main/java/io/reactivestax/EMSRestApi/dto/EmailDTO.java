package io.reactivestax.EMSRestApi.dto;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class EmailDTO {
    private Long id;
    private Long clientId;
    private String to;
    private String subject;
    private String body;

}
