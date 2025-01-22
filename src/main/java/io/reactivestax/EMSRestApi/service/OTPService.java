package io.reactivestax.EMSRestApi.service;


import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Otp;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.exception.ExceededGenerationCountError;
import io.reactivestax.EMSRestApi.exception.ExceededValidationCountError;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    private static final int MAX_OTP_GENERATION_ATTEMPTS = 5;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    private static final int OTP_BLOCK_TIME_HOURS = 8; // Block user for 8 hours if max OTP generations are reached
    private static final int VERIFICATION_BLOCK_TIME_HOURS = 2; //Block the user for 2 hours


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
//    public OtpDTO createOtpForEmail(OtpDTO otpDTO) {
//        Otp otp = converToOtp(otpDTO);
//        return convertToOtpDTO(otpRepository.save(otp));
//    }
//
//    public OtpDTO createOtpForSms(OtpDTO otpDTO) {
//        Otp otp = converToOtp(otpDTO);
//        return convertToOtpDTO(otpRepository.save(otp));
//    }
//
//    public OtpDTO createOtpForPhone(OtpDTO otpDTO) {
//        Otp otp = converToOtp(otpDTO);
//        return convertToOtpDTO(otpRepository.save(otp));
//    }
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

    private Otp converToOtp(OtpDTO otpDTO) {
        return Otp
                .builder()
                .id(otpDTO.getOtpId())
                .validOtp(otpDTO.getValidOtp())
                .createdAt(otpDTO.getCreatedAt())
                .lastAccessed(otpDTO.getLastAccessed())
                .validationRetryCount(otpDTO.getValidationRetryCount())
                .isValid(otpDTO.getIsValid())
                .phone(otpDTO.getPhone())
                .email(otpDTO.getEmail())
                .client(fetchClientById(otpDTO.getClientId()))
                .build();
    }

    private Client fetchClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }


    public OtpDTO createOtpForSms(OtpDTO otpDTO) {
        return createOtp(otpDTO, "sms");
    }

    public OtpDTO createOtpForPhone(OtpDTO otpDTO) {
        return createOtp(otpDTO, "call");
    }

    public OtpDTO createOtpForEmail(OtpDTO otpDTO) {
        return createOtp(otpDTO, "email");
    }

    private OtpDTO createOtp(OtpDTO otpDTO, String type) {
        Client client = clientRepository.findById(otpDTO.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));

        // Check if the client is locked
        if (client.getIsLocked() != null && client.getIsLocked()) {
            throw new RuntimeException("Client is locked due to multiple OTP generation attempts.");
        }

        Otp otp = client.getOtp();

        // Check OTP generation attempts limit
        if (otp != null && otp.getGenerationRetryCount() >= MAX_OTP_GENERATION_ATTEMPTS) {
            if (otp.getCreatedAt().plusHours(OTP_BLOCK_TIME_HOURS).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Max OTP generation attempts reached. Try again later.");
            } else {
                otp.setGenerationRetryCount(0);  // Reset the counter after 8 hours
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
            otp.setValidOtp(generateRandomOtp());  // New OTP
            otp.setCreatedAt(LocalDateTime.now()); // Reset creation time
        }

        otp.setGenerationRetryCount(otp.getGenerationRetryCount() + 1);
        otpRepository.save(otp);

        client.setOtp(otp);
        clientRepository.save(client);

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

            if (otp != null && otp.getValidationRetryCount() >= MAX_VERIFICATION_ATTEMPTS) {
                handleVerificationBlocking(client);
            }

            throw new RuntimeException("Invalid OTP");
        }

        // Reset validation retry count if OTP is valid
        otp.setValidationRetryCount(0);
        otpRepository.save(otp);

        return convertToOtpDTO(otp);
    }

    private void handleVerificationBlocking(Client client) {
        // Add logic for sliding window blocking.
        LocalDateTime lastAccessed = client.getOtp().getLastAccessed();
        if (lastAccessed != null && ChronoUnit.HOURS.between(lastAccessed, LocalDateTime.now()) < VERIFICATION_BLOCK_TIME_HOURS) {
            throw new RuntimeException("Client is blocked for verification due to multiple failed attempts.");
        }

        // Lock the client for sliding window
        client.setIsLocked(true);
        clientRepository.save(client);
        //Pending to unlock the user after 2 hours .. Have to use the scheduler
    }

    public OtpDTO statusForOTP(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        Otp otp = client.getOtp();
        return convertToOtpDTO(otp);
    }

    private String generateRandomOtp() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000)); //generating the 6 digit otp......
    }


}
