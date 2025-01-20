package io.reactivestax.EMSRestApi.service.otp;


import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.ems.Contact;
import io.reactivestax.EMSRestApi.domain.ems.Otp;
import io.reactivestax.EMSRestApi.dto.ems.ClientDTO;
import io.reactivestax.EMSRestApi.dto.ems.OtpDTO;
import io.reactivestax.EMSRestApi.exception.ExceededGenerationCountError;
import io.reactivestax.EMSRestApi.exception.ExceededValidationCountError;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ems.OTPRepository;
import io.reactivestax.EMSRestApi.service.ems.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    public boolean isValid(OtpDTO otpDTO) {
        if (otpDTO.getGenerationRetryCount() > 5) {
            //Logic to lock the user for 8 hrs....
            //calculate the 8 hrs from the time of creation
            LocalDateTime unlockAfterTime = otpDTO.getCreatedAt().plusHours(8);


            //then we can run the scheduler after 8 hrs to unlock...
            //scheduler.unlockAfter(unlockAfterTime)
            throw new ExceededGenerationCountError("Otp generation attempts exceeded");
        }

        if (otpDTO.getValidationRetryCount() > 3) {
            //Logic to lock the user for 2 hours..
            throw new ExceededValidationCountError("Max OTP validation attempts reached");
        }
        otpDTO.setOtpAttempts(otpDTO.getOtpAttempts() + 1);
        return true;
    }
    public OtpDTO save(OtpDTO otpDTO) {
        Otp otp = converToOtp(otpDTO);
        return convertToOtpDTO(otpRepository.save(otp));
    }

    private OtpDTO convertToOtpDTO(Otp otp) {
        return OtpDTO
                .builder()
                .otpId(otp.getOtpId())
                .validOtp(otp.getValidOtp())
                .createdAt(otp.getCreatedAt())
                .lastAccessed(otp.getLastAccessed())
                .validationRetryCount(otp.getValidationRetryCount())
                .isValid(otp.getIsValid())
                .phone(otp.getPhone())
                .email(otp.getEmail())
                .clientId(otp.getClient().getClientId())
                .build();
    }

    private Otp converToOtp(OtpDTO otpDTO){
        return Otp
                .builder()
                .otpId(otpDTO.getOtpId())
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

    private Client fetchClientById(Long clientId){
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

}
