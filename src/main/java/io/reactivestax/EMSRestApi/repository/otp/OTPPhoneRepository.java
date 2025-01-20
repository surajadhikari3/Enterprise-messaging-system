package io.reactivestax.EMSRestApi.repository.otp;

import io.reactivestax.EMSRestApi.domain.otp.OTPPhone;
import io.reactivestax.EMSRestApi.domain.otp.OTPSms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPPhoneRepository extends JpaRepository<OTPPhone, Long> {
}
