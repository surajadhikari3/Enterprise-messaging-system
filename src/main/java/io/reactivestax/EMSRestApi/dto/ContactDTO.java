package io.reactivestax.EMSRestApi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDTO {
    private long contactId;
    private String email;
    private String phone;
    private Long clientId;
}
