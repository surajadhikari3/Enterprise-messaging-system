package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Sms, Long> {
}
