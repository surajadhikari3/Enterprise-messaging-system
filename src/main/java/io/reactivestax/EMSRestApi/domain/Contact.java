package io.reactivestax.EMSRestApi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contact {
    private long contactId;
    private String email;
    private String phone;
    private String customer;
}
