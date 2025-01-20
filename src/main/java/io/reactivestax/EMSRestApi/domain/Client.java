package io.reactivestax.EMSRestApi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.EMSRestApi.dto.ContactDTO;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    @OneToMany(mappedBy = "contact_id")
    @JsonBackReference
    private List<ContactDTO> contactDTOS = new ArrayList<>();
    private OtpDTO otpDTO;
}
