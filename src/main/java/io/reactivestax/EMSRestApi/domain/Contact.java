package io.reactivestax.EMSRestApi.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long contactId;
    private String email;
    private String phone;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    @JsonManagedReference
    private String customer;
}
