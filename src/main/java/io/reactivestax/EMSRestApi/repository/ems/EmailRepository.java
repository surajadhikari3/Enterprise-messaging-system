package io.reactivestax.EMSRestApi.repository.ems;

import io.reactivestax.EMSRestApi.domain.ems.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
