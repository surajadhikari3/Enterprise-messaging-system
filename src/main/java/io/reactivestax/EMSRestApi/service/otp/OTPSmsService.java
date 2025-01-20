package io.reactivestax.EMSRestApi.service.otp;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.otp.OTPSms;
import io.reactivestax.EMSRestApi.dto.otp.OTPSmsDTO;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.otp.OTPSmsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OTPSmsService {

    @Autowired
    private OTPSmsRepository otpSmsRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<OTPSmsDTO> findAll(){
        return otpSmsRepository.findAll().stream().map(this::convertToOTPSmsDTO).collect(Collectors.toList());
    }

    public OTPSmsDTO save(OTPSmsDTO otpSmsDTO){
        OTPSms otpSms = converToOTPSms(otpSmsDTO);
        return convertToOTPSmsDTO(otpSmsRepository.save(otpSms));
    }


    public Optional<OTPSmsDTO> findById(Long id){
        return otpSmsRepository.findById(id).map(this::convertToOTPSmsDTO);
    }

    public void deleteById(Long id){
        otpSmsRepository.deleteById(id);
    }


    private OTPSmsDTO convertToOTPSmsDTO(OTPSms otpSms){
        OTPSmsDTO otpSmsDTO = new OTPSmsDTO();
        otpSmsDTO.setId(otpSms.getId());
        otpSmsDTO.setPhone(otpSms.getPhone());
        return otpSmsDTO;
    }

    private OTPSms converToOTPSms(OTPSmsDTO otpSmsDTO){
        OTPSms otpSms = new OTPSms();
        otpSms.setId(otpSmsDTO.getId());
        otpSms.setClient(fetchClientById(otpSmsDTO.getClientId()));
        return otpSms;
    }

    private Client fetchClientById(Long clientId){
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
}
