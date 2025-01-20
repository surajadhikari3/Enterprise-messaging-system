package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Client;
import io.reactivestax.EMSRestApi.domain.Email;
import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.dto.SmsDTO;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.EmailRepository;
import io.reactivestax.EMSRestApi.repository.SmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<EmailDTO> findAll(){
        return emailRepository.findAll().stream().map(this::convertToEmailDTO).collect(Collectors.toList());
    }

    public EmailDTO save(EmailDTO emailDTO){
        Email email = converToEmail(emailDTO);
        return convertToEmailDTO(emailRepository.save(email));
    }


    public Optional<EmailDTO> findById(Long id){
        return emailRepository.findById(id).map(this::convertToEmailDTO);
    }

    public void deleteById(Long id){
         emailRepository.deleteById(id);
    }


    private EmailDTO convertToEmailDTO(Email email){
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(email.getId());
        emailDTO.setTo(email.getTo());
        emailDTO.setSubject(email.getSubject());
        emailDTO.setBody(email.getBody());
        emailDTO.setClientId(email.getClient().getClientId());
        return emailDTO;
    }

    private Email converToEmail(EmailDTO emailDTO){
        Email email = new Email();
        email.setId(emailDTO.getId());
        email.setTo(emailDTO.getTo());
        email.setSubject(emailDTO.getSubject());
        email.setBody(emailDTO.getBody());
        email.setClient(fetchClientById(emailDTO.getClientId()));
        return email;
    }


    private Client fetchClientById(Long clientId){
       return clientRepository.findById(clientId)
               .orElseThrow(() -> new RuntimeException("Client not found"));
    }

}
