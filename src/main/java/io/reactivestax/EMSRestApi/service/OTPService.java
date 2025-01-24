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
        Client client = clientRepository.findById(otpDTO.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        Otp otp = client.getOtp();

        //checking the OTP is brand new or not and  OTP generation limits
        if (otp != null && otp.getGenerationRetryCount() >= maxOtpGenerationAttempts) {
            client.setIsLocked(true);
            if (otp.getCreatedAt().plusHours(otpBlockTimeHours).isAfter(LocalDateTime.now())) {
                throw new ExceededGenerationException("Max OTP generation attempts reached. Try again later.");
            } else {
                otp.setGenerationRetryCount(0);
                client.setIsLocked(false);
            }
        }

        if (otp == null) {
            otp = new Otp();
            otp.setPhone(otpDTO.getPhone());
            otp.setEmail(otpDTO.getEmail());
            otp.setClient(client);
            otp.setValidOtp(generateRandomOtp());
            otp.setOtpStatus(Status.VALID);
            otp.setVerificationStatus(Status.PENDING);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setLastAccessed(LocalDateTime.now());
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
        client.setOtp(otp);
        clientRepository.save(client);
        otpRepository.save(otp);
        artemisProducer.sendMessage(queueName, String.valueOf(otp.getId()), type); //publishing the otp..
        return convertToOtpDTO(otp);
    }

    public OtpDTO verifyOtp(OtpDTO otpDTO) {
        Client client = clientRepository.findById(otpDTO.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        Otp otp = client.getOtp();
        if (otp == null || otp.getOtpStatus().equals(Status.INVALID) || otp.getValidOtp() == null || !otp.getValidOtp().equals(otpDTO.getValidOtp())) {
            if (otp != null) {
                otp.setValidationRetryCount(otp.getValidationRetryCount() + 1);
                otpRepository.save(otp);
            }
            if (otp != null && otp.getValidationRetryCount() >= maxVerificationAttempts) {
                handleVerificationBlocking(client);
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
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        Otp otp = client.getOtp();
        if (otp != null && otp.getValidationRetryCount() >= maxVerificationAttempts && handleVerificationBlocking(client)) {
            return Status.INVALID;
        }
        assert otp != null;
        if (otp.getLastAccessed().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return Status.EXPIRED;
        }
        return Status.VALID;
    }

    private boolean handleVerificationBlocking(Client client) {
        LocalDateTime lastAccessed = client.getOtp().getLastAccessed();
        if (lastAccessed != null && ChronoUnit.HOURS.between(lastAccessed, LocalDateTime.now()) < verificationBlockTimeHours) {
            client.setIsLocked(true);
            clientRepository.save(client);
            return true;
        }
        client.setIsLocked(false);
        clientRepository.save(client);
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
                .clientId(otp.getClient() != null ? otp.getClient().getId() : null)
                .build();
    }

    private String generateRandomOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(0, 1000000));
    }
}
