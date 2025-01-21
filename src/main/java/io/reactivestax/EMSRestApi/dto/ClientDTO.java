package io.reactivestax.EMSRestApi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientDTO {
    private Long clientId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    private Boolean isLocked;
    private List<Long> contactId;
    private Long otpId;
}
