package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
