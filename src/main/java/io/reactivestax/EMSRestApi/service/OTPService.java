package io.reactivestax.EMSRestApi.service;


import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Otp;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.reactivestax.EMSRestApi.exception.ExceededGenerationCountException;
import io.reactivestax.EMSRestApi.exception.ExceededValidationCountException;
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


//    public boolean isValid(OtpDTO otpDTO) {
//        if (otpDTO.getGenerationRetryCount() > 5) {
//            //Logic to lock the user for 8 hrs....
//            //calculate the 8 hrs from the time of creation
//            LocalDateTime unlockAfterTime = otpDTO.getCreatedAt().plusHours(8);
//
//
//            //then we can run the scheduler after 8 hrs to unlock...
//            //scheduler.unlockAfter(unlockAfterTime)
//            throw new ExceededGenerationCountError("Otp generation attempts exceeded");
//        }
//
//        if (otpDTO.getValidationRetryCount() > 3) {
//            //Logic to lock the user for 2 hours..
//            throw new ExceededValidationCountError("Max OTP validation attempts reached");
//        }

    /// /        otpDTO.setOtpAttempts(otpDTO.getOtpAttempts() + 1);
//        return true;
//    }
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
        // Check if the client is locked
        if (client.getIsLocked() != null && client.getIsLocked()) {
            throw new ExceededGenerationCountException("Client is locked due to multiple OTP generation attempts.");
        }
        Otp otp = client.getOtp();

        //checking the OTP is brand new or not and  OTP generation limits
        if (otp != null && otp.getGenerationRetryCount() >= maxOtpGenerationAttempts) {
            if (otp.getCreatedAt().plusHours(otpBlockTimeHours).isAfter(LocalDateTime.now())) {
                throw new ExceededGenerationCountException("Max OTP generation attempts reached. Try again later.");
            } else {
                otp.setGenerationRetryCount(0);  // Here Reseting the counter after 8 hours
            }
        }
        if (otp == null) {
            otp = new Otp();
            otp.setPhone(otpDTO.getPhone());
            otp.setEmail(otpDTO.getEmail());
            otp.setClient(client);
            otp.setValidOtp(generateRandomOtp());
            otp.setIsValid(true);
            otp.setCreatedAt(LocalDateTime.now());
        } else {
            //logic to check if the otp is expired....
            if (otp.getCreatedAt().plusMinutes(2).isBefore(LocalDateTime.now())) { // since the validity of the otp is 2 minutes..
                otp.setValidOtp(generateRandomOtp());  // New OTP
                otp.setCreatedAt(LocalDateTime.now()); // Reset creation time
            } else {
                otp.setLastAccessed(LocalDateTime.now());
            }
        }

        otp.setGenerationRetryCount(otp.getGenerationRetryCount() + 1);
        otpRepository.save(otp);

        client.setOtp(otp);
        clientRepository.save(client);
        artemisProducer.sendMessage(queueName, String.valueOf(otp.getId()), type); //publishing the otp..
        return convertToOtpDTO(otp);
    }

    public OtpDTO verifyOtp(OtpDTO otpDTO) {
        Client client = clientRepository.findById(otpDTO.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));

        Otp otp = client.getOtp();
        if (otp == null || !otp.getIsValid() || otp.getValidOtp() == null || !otp.getValidOtp().equals(otpDTO.getValidOtp())) {
            if (otp != null) {
                otp.setValidationRetryCount(otp.getValidationRetryCount() + 1);
                otpRepository.save(otp);
            }

            if (otp != null && otp.getValidationRetryCount() >= maxVerificationAttempts) {
                handleVerificationBlocking(client);
            }
            throw new InvalidOTPException("Invalid OTP");
        }

        // Reset validation retry count if OTP is valid
        otp.setValidationRetryCount(0);
        otpRepository.save(otp);
        return convertToOtpDTO(otp);
    }

    private void handleVerificationBlocking(Client client) {
        //pending to add sliding window logic as per discussion in class....
        LocalDateTime lastAccessed = client.getOtp().getLastAccessed();
        if (lastAccessed != null && ChronoUnit.HOURS.between(lastAccessed, LocalDateTime.now()) < verificationBlockTimeHours) {
            throw new ExceededValidationCountException("Client is blocked for verification due to multiple failed attempts.");
        }
        client.setIsLocked(true);
        clientRepository.save(client);
        //Pending to unlock the user after 2 hours .. Have to use the scheduler
    }

    public Status statusForOTP(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        handleVerificationBlocking(client);
        if (Boolean.TRUE.equals(client.getIsLocked())) {
            return Status.INVALID;
        }
        return Status.VALID;
    }

    private OtpDTO convertToOtpDTO(Otp otp) {
        return OtpDTO
                .builder()
                .otpId(otp.getId())
                .validOtp(otp.getValidOtp())
                .createdAt(otp.getCreatedAt())
                .lastAccessed(otp.getLastAccessed())
                .validationRetryCount(otp.getValidationRetryCount())
                .isValid(otp.getIsValid())
                .phone(otp.getPhone())
                .email(otp.getEmail())
                .clientId(otp.getClient() != null ? otp.getClient().getId() : null)
                .build();
    }

    private String generateRandomOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(0, 1000000));
    }
}
