package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
