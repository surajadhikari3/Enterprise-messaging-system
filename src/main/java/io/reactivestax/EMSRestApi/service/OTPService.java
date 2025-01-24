package io.reactivestax.EMSRestApi.service;


import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Otp;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.exception.ExceededGenerationException;
import io.reactivestax.EMSRestApi.exception.ExceededValidationException;
import io.reactivestax.EMSRestApi.exception.InvalidOTPException;
import io.reactivestax.EMSRestApi.message_broker.ArtemisProducer;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ArtemisProducer artemisProducer;


    @Value("${otp.max-otp-generation-attempts}")
    private int maxOtpGenerationAttempts;

    @Value("${otp.max-verification-attempts}")
    private int maxVerificationAttempts;

    @Value("${otp.block-time-hours}")
    private int otpBlockTimeHours;

    @Value("${otp.verification-block-time-hours}")
    private int verificationBlockTimeHours;

    @Value("${queue.name}")
    private String queueName;


    public OtpDTO createOtpForSms(OtpDTO otpDTO) {
        return createOtp(otpDTO, "otp-sms");
    }

    public OtpDTO createOtpForPhone(OtpDTO otpDTO) {
        return createOtp(otpDTO, "otp-call");
    }

    public OtpDTO createOtpForEmail(OtpDTO otpDTO) {
        return createOtp(otpDTO, "otp-email");
    }

    private OtpDTO createOtp(OtpDTO otpDTO, String type) {
        Otp otp = otpRepository.findOtpByClientId(otpDTO.getClientId());

        //checking the OTP is brand new or not and  OTP generation limits
        if (otp != null && otp.getGenerationRetryCount() >= maxOtpGenerationAttempts) {
            otp.setIsLocked(true);
            if (otp.getCreatedAt().plusHours(otpBlockTimeHours).isAfter(LocalDateTime.now())) {
                throw new ExceededGenerationException("Max OTP generation attempts reached. Try again later.");
            } else {
                otp.setGenerationRetryCount(0);
                otp.setIsLocked(false);
            }
        }

        if (otp == null) {
            otp = new Otp();
            otp.setPhone(otpDTO.getPhone());
            otp.setEmail(otpDTO.getEmail());
            otp.setValidOtp(generateRandomOtp());
            otp.setOtpStatus(Status.VALID);
            otp.setVerificationStatus(Status.PENDING);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setLastAccessed(LocalDateTime.now());
            otp.setClientId(otpDTO.getClientId());
        } else {
            //logic to check if the otp is expired....
            if (otp.getCreatedAt().plusMinutes(2).isBefore(LocalDateTime.now())) { // since the validity of the otp is 2 minutes..
                otp.setValidOtp(generateRandomOtp());  // New OTP
                otp.setOtpStatus(Status.VALID);
                otp.setCreatedAt(LocalDateTime.now()); // Reset creation time
                otp.setLastAccessed(LocalDateTime.now());
            } else {
                otp.setLastAccessed(LocalDateTime.now());
            }
        }
        otp.setGenerationRetryCount(otp.getGenerationRetryCount() + 1);
        otpRepository.save(otp);
        artemisProducer.sendMessage(queueName, String.valueOf(otp.getId()), type); //publishing the otp..
        return convertToOtpDTO(otp);
    }

    public OtpDTO verifyOtp(OtpDTO otpDTO) {
        Otp otp = otpRepository.findOtpByClientId(otpDTO.getClientId());
        if (otp == null || otp.getOtpStatus().equals(Status.INVALID) || otp.getValidOtp() == null || !otp.getValidOtp().equals(otpDTO.getValidOtp())) {
            if (otp != null) {
                otp.setValidationRetryCount(otp.getValidationRetryCount() + 1);
                otpRepository.save(otp);
            }
            if (otp != null && otp.getValidationRetryCount() >= maxVerificationAttempts) {
                handleVerificationBlocking(otp);
            }
            throw new InvalidOTPException("Invalid OTP");
        }
        otp.setVerificationStatus(Status.VERIFIED);
        otp.setValidationRetryCount(0);
        otp.setGenerationRetryCount(0);
        otpRepository.save(otp);
        return convertToOtpDTO(otp);
    }


    public Status statusForOTP(Long clientId) {
        Otp otp = otpRepository.findOtpByClientId(clientId);
        if (otp != null && otp.getValidationRetryCount() >= maxVerificationAttempts && handleVerificationBlocking(otp)) {
            return Status.INVALID;
        }
        assert otp != null;
        if (otp.getLastAccessed().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return Status.EXPIRED;
        }
        return Status.VALID;
    }

    private boolean handleVerificationBlocking(Otp otp) {
        LocalDateTime lastAccessed = otp.getLastAccessed();
        if (lastAccessed != null && ChronoUnit.HOURS.between(lastAccessed, LocalDateTime.now()) < verificationBlockTimeHours) {
            otp.setIsLocked(true);
            otpRepository.save(otp);
            return true;
        }
        otp.setIsLocked(false);
        otpRepository.save(otp);
        return false;
    }

    private OtpDTO convertToOtpDTO(Otp otp) {
        return OtpDTO
                .builder()
                .otpId(otp.getId())
                .validOtp(otp.getValidOtp())
                .createdAt(otp.getCreatedAt())
                .lastAccessed(otp.getLastAccessed())
                .generationRetryCount(otp.getGenerationRetryCount())
                .verificationStatus(otp.getVerificationStatus())
                .validationRetryCount(otp.getValidationRetryCount())
                .otpStatus(otp.getOtpStatus())
                .validOtp(otp.getValidOtp())
                .phone(otp.getPhone())
                .email(otp.getEmail())
                .isLocked(otp.getIsLocked())
                .build();
    }

    private Otp convertToOtp(OtpDTO otp) {
        return Otp
                .builder()
                .id(otp.getOtpId())
                .validOtp(otp.getValidOtp())
                .createdAt(otp.getCreatedAt())
                .lastAccessed(otp.getLastAccessed())
                .generationRetryCount(otp.getGenerationRetryCount())
                .verificationStatus(otp.getVerificationStatus())
                .validationRetryCount(otp.getValidationRetryCount())
                .otpStatus(otp.getOtpStatus())
                .validOtp(otp.getValidOtp())
                .phone(otp.getPhone())
                .email(otp.getEmail())
                .isLocked(otp.getIsLocked())
                .build();
    }

    private String generateRandomOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(0, 1000000));
    }
}
