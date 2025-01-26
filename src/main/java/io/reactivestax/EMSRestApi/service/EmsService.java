package io.reactivestax.EMSRestApi.service;

import io.reactivestax.EMSRestApi.domain.Email;
import io.reactivestax.EMSRestApi.domain.Phone;
import io.reactivestax.EMSRestApi.domain.Sms;
import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.dto.PhoneDTO;
import io.reactivestax.EMSRestApi.dto.SmsDTO;
import io.reactivestax.EMSRestApi.repository.ClientRepository;
import io.reactivestax.EMSRestApi.repository.EmailRepository;
import io.reactivestax.EMSRestApi.repository.PhoneRepository;
import io.reactivestax.EMSRestApi.repository.SmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmsService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OTPService otpService;


    public EmailDTO saveEmail(EmailDTO emailDTO) {
        Email email = converToEmail(emailDTO);
        return convertToEmailDTO(emailRepository.save(email));
    }

    public PhoneDTO savePhone(PhoneDTO phoneDTO) {
        Phone phone = converToPhone(phoneDTO);
        return convertToPhoneDTO(phoneRepository.save(phone));
    }

    public SmsDTO saveSms(SmsDTO smsDTO) {
        Sms sms = converToSms(smsDTO);
        return convertToSmsDTO(smsRepository.save(sms));
    }

    private EmailDTO convertToEmailDTO(Email email) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(email.getId());
        emailDTO.setReceiverEmailId(email.getReceiverEmailId());
        emailDTO.setSubject(email.getSubject());
        emailDTO.setBody(email.getBody());
        emailDTO.setClientId(email.getClientId());
        return emailDTO;
    }

    private Email converToEmail(EmailDTO emailDTO) {
        Email email = new Email();
        email.setId(emailDTO.getId());
        email.setReceiverEmailId(emailDTO.getReceiverEmailId());
        email.setSubject(emailDTO.getSubject());
        email.setBody(emailDTO.getBody());
        email.setClientId(emailDTO.getClientId());
        return email;
    }

    private PhoneDTO convertToPhoneDTO(Phone phone) {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setId(phone.getId());
        phoneDTO.setOutgoingPhoneNumber(phone.getOutgoingPhoneNumber());
        phoneDTO.setClientId(phone.getClientId());
        return phoneDTO;
    }

    private Phone converToPhone(PhoneDTO phoneDTO) {
        Phone phone = new Phone();
        phone.setId(phoneDTO.getId());
        phone.setOutgoingPhoneNumber(phoneDTO.getOutgoingPhoneNumber());
        phone.setClientId(phoneDTO.getClientId());
        return phone;
    }


    private SmsDTO convertToSmsDTO(Sms sms) {
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setId(sms.getId());
        smsDTO.setPhone(sms.getPhone());
        smsDTO.setMessage(sms.getMessage());
        smsDTO.setClientId(sms.getClientId());
        return smsDTO;
    }

    private Sms converToSms(SmsDTO smsDTO) {
        Sms sms = new Sms();
        sms.setId(smsDTO.getId());
        sms.setPhone(smsDTO.getPhone());
        sms.setMessage(smsDTO.getMessage());
        sms.setClientId(smsDTO.getClientId());
        return sms;
    }

}
