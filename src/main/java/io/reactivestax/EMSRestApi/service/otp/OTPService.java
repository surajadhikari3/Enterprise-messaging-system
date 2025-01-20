package io.reactivestax.EMSRestApi.service.otp;


import io.reactivestax.EMSRestApi.dto.ems.OtpDTO;
import io.reactivestax.EMSRestApi.exception.ExceededGenerationCountError;
import io.reactivestax.EMSRestApi.exception.ExceededValidationCountError;
import io.reactivestax.EMSRestApi.repository.ems.OTPRepository;
import io.reactivestax.EMSRestApi.service.ems.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientService clientService;

    public boolean isValid(OtpDTO otpDTO){
       if(otpDTO.getGenerationRetryCount() > 5){
           //Logic to lock the user for 8 hrs....
          //calculate the 8 hrs from the time of creation
           LocalDateTime unlockAfterTime = otpDTO.getCreatedAt().plusHours(8);


           //then we can run the scheduler after 8 hrs to unlock...
           //scheduler.unlockAfter(unlockAfterTime)
           throw new ExceededGenerationCountError("Otp generation attempts exceeded");
       }

       if(otpDTO.getValidationRetryCount() > 3) {
           //Logic to lock the user for 2 hours..
           throw new ExceededValidationCountError("Max OTP validation attempts reached");
       }
       otpDTO.setOtpAttempts(otpDTO.getOtpAttempts() + 1);

    }
}
