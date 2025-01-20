package io.reactivestax.EMSRestApi.repository.ems;

import io.reactivestax.EMSRestApi.domain.ems.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
