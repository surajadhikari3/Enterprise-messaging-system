package io.reactivestax.EMSRestApi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonBackReference
    @ToString.Exclude
    private List<Contact> contacts = new ArrayList<>();


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "otp_id", referencedColumnName = "id")
//    @JsonManagedReference
//    @ToString.Exclude
//    private Otp otp;
}
