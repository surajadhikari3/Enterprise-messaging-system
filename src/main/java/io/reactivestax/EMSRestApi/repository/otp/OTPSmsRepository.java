package io.reactivestax.EMSRestApi.repository.otp;

import io.reactivestax.EMSRestApi.domain.otp.OTPEmail;
import io.reactivestax.EMSRestApi.domain.otp.OTPSms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPSmsRepository extends JpaRepository<OTPSms, Long> {
}
