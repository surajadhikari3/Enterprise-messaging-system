package io.reactivestax.EMSRestApi.repository;

import io.reactivestax.EMSRestApi.domain.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<Otp, Long> {
    Otp findOtpByClientId(Long clientId);
}
