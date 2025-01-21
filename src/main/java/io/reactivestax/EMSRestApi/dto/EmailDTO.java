package io.reactivestax.EMSRestApi.dto;

import lombok.Data;

@Data
public class EmailDTO {
    private Long id;
    private Long clientId;
    private String to;
    private String subject;
    private String body;

}
