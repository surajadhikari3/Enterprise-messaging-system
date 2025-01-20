package io.reactivestax.EMSRestApi.service.ems;

import io.reactivestax.EMSRestApi.domain.ems.Client;
import io.reactivestax.EMSRestApi.domain.ems.Sms;
import io.reactivestax.EMSRestApi.dto.ems.SmsDTO;
import io.reactivestax.EMSRestApi.repository.ems.ClientRepository;
import io.reactivestax.EMSRestApi.repository.ems.SmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SmsService {

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<SmsDTO> findAll(){
        return smsRepository.findAll().stream().map(this::convertToSmsDTO).collect(Collectors.toList());
    }

    public SmsDTO save(SmsDTO smsDTO){
        Sms sms = converToSms(smsDTO);
        return convertToSmsDTO(smsRepository.save(sms));
    }
    
    
    public Optional<SmsDTO> findById(Long id){
        return smsRepository.findById(id).map(this::convertToSmsDTO);
    }
    
    public void deleteById(Long id){
         smsRepository.deleteById(id);
    }
    

    private SmsDTO convertToSmsDTO(Sms sms){
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setId(sms.getId());
        smsDTO.setPhone(sms.getPhone());
        smsDTO.setMessage(smsDTO.getPhone());
        smsDTO.setClientId(sms.getClient().getClientId());
        return smsDTO;
    }

    private Sms converToSms(SmsDTO smsDTO){
        Sms sms = new Sms();
        sms.setId(smsDTO.getId());
        sms.setPhone(smsDTO.getPhone());
        sms.setMessage(smsDTO.getMessage());
        sms.setClient(fetchClientById(smsDTO.getClientId()));
        return sms;
    }


    private Client fetchClientById(Long clientId){
       return clientRepository.findById(clientId)
               .orElseThrow(() -> new RuntimeException("Client not found"));
    }

}
