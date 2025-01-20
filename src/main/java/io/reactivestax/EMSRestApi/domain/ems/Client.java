package io.reactivestax.EMSRestApi.domain.ems;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.reactivestax.EMSRestApi.dto.ems.ContactDTO;
import io.reactivestax.EMSRestApi.dto.ems.OtpDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
    private Boolean isLocked;

    @OneToMany(mappedBy = "contact")
    @JsonBackReference
    @ToString.Exclude
    private List<Contact> contact = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "otp_id", referencedColumnName = "id")
    @JsonManagedReference
    @ToString.Exclude
    private Otp otp;
}
