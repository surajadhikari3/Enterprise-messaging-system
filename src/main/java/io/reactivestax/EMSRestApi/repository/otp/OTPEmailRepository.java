package io.reactivestax.EMSRestApi.repository.otp;

import io.reactivestax.EMSRestApi.domain.ems.Email;
import io.reactivestax.EMSRestApi.domain.otp.OTPEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPEmailRepository extends JpaRepository<OTPEmail, Long> {
}
