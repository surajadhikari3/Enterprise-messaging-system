package io.reactivestax.EMSRestApi.service.otp;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.otp.OTPEmail;
import io.reactivestax.EMSRestApi.dto.ems.OtpDTO;
import io.reactivestax.EMSRestApi.dto.otp.OTPEmailDTO;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ems.OTPRepository;
import io.reactivestax.EMSRestApi.repository.otp.OTPEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OTPEmailService {

    @Autowired
    private OTPEmailRepository otpEmailRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<OTPEmailDTO> findAll(){
        return otpEmailRepository.findAll().stream().map(this::convertToOTPEmailDTO).collect(Collectors.toList());
    }

//    public OTPEmailDTO save(OTPEmailDTO otpEmailDTO){
//        OTPEmail otpEmail = converToOTPEmail(otpEmailDTO);
//        return convertToOTPEmailDTO(otpEmailRepository.save(otpEmail));
//    }

//    public OTPEmailDTO save(OtpDTO otpDTO){
//        if(otpDTO.isValid()) {
//            OTPEmail otpEmail = converToOTPEmail(otpEmailDTO);
//            return convertToOTPEmailDTO(otpEmailRepository.save(otpEmail));
//        }
//    }


    public Optional<OTPEmailDTO> findById(Long id){
        return otpEmailRepository.findById(id).map(this::convertToOTPEmailDTO);
    }

    public void deleteById(Long id){
        otpEmailRepository.deleteById(id);
    }


    private OTPEmailDTO convertToOTPEmailDTO(OTPEmail otpEmail){
        OTPEmailDTO otpEmailDTO = new OTPEmailDTO();
        otpEmailDTO.setId(otpEmail.getId());
        otpEmailDTO.setEmail(otpEmailDTO.getEmail());
        return otpEmailDTO;
    }

    private OTPEmail converToOTPEmail(OTPEmailDTO otpEmailDTO){
        OTPEmail otpEmail = new OTPEmail();
        otpEmail.setId(otpEmailDTO.getId());
        otpEmail.setClient(fetchClientById(otpEmailDTO.getClientId()));
        return otpEmail;
    }


    private Client fetchClientById(Long clientId){
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
}
