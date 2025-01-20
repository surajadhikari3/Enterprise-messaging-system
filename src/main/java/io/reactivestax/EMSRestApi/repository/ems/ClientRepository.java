package io.reactivestax.EMSRestApi.repository.ems;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
