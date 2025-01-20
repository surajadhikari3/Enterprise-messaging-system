package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Phone;
import io.reactivestax.EMSRestApi.dto.PhoneDTO;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<PhoneDTO> findAll(){
        return phoneRepository.findAll().stream().map(this::convertToPhoneDTO).collect(Collectors.toList());
    }

    public PhoneDTO save(PhoneDTO phoneDTO){
        Phone phone = converToPhone(phoneDTO);
        return convertToPhoneDTO(phoneRepository.save(phone));
    }
    
    
    public Optional<PhoneDTO> findById(Long id){
        return phoneRepository.findById(id).map(this::convertToPhoneDTO);
    }
    
    public void deleteById(Long id){
         phoneRepository.deleteById(id);
    }
    

    private PhoneDTO convertToPhoneDTO(Phone phone){
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setId(phone.getId());
        phoneDTO.setOutgoingPhoneNumber(phone.getOutgoingPhoneNumber());
        phoneDTO.setClientId(phone.getClient().getClientId());
        return phoneDTO;
    }

    private Phone converToPhone(PhoneDTO phoneDTO){
        Phone phone = new Phone();
        phone.setId(phoneDTO.getId());
        phone.setOutgoingPhoneNumber(phoneDTO.getOutgoingPhoneNumber());
        phone.setClient(fetchClientById(phoneDTO.getClientId()));
        return phone;
    }


    private Client fetchClientById(Long clientId){
       return clientRepository.findById(clientId)
               .orElseThrow(() -> new RuntimeException("Client not found"));
    }

}
