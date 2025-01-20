package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Email;
import io.reactivestax.EMSRestApi.domain.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
