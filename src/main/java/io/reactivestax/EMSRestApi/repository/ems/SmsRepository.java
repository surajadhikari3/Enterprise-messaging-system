package io.reactivestax.EMSRestApi.repository.ems;

import io.reactivestax.EMSRestApi.domain.ems.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Sms, Long> {
}
