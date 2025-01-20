package io.reactivestax.EMSRestApi.dto.ems;

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
    private List<Long> contactId;
}
