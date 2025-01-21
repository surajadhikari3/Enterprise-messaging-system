package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
