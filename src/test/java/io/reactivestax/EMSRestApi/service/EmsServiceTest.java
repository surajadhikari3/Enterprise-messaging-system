package io.reactivestax.EMSRestApi.service;


import io.reactivestax.EMSRestApi.domain.Email;
import io.reactivestax.EMSRestApi.domain.Phone;
import io.reactivestax.EMSRestApi.domain.Sms;
import io.reactivestax.EMSRestApi.dto.EmailDTO;
import io.reactivestax.EMSRestApi.dto.PhoneDTO;
import io.reactivestax.EMSRestApi.dto.SmsDTO;
import io.reactivestax.EMSRestApi.repository.EmailRepository;
import io.reactivestax.EMSRestApi.repository.PhoneRepository;
import io.reactivestax.EMSRestApi.repository.SmsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class EmsServiceTest {

    @Autowired
    private EmsService emsService;

    @MockitoBean
    private EmailRepository emailRepository;

    @MockitoBean
    private PhoneRepository phoneRepository;

    @MockitoBean
    private SmsRepository smsRepository;

    @Test
    void testSaveEmail() {
        // Arrange
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(1L);
        emailDTO.setReceiverEmailId("suraj@fullstack.com");
        emailDTO.setSubject("Test Subject");
        emailDTO.setBody("Test Body");
        emailDTO.setClientId(1001L);

        Email email = new Email();
        email.setId(1L);
        email.setReceiverEmailId("suraj@fullstack.com");
        email.setSubject("Test Subject");
        email.setBody("Test Body");
        email.setClientId(1001L);

        Mockito.when(emailRepository.save(any(Email.class))).thenReturn(email);

        // Act
        EmailDTO savedEmailDTO = emsService.saveEmail(emailDTO);

        // Assert
        assertThat(savedEmailDTO).isNotNull();
        assertThat(savedEmailDTO.getId()).isEqualTo(emailDTO.getId());
        assertThat(savedEmailDTO.getReceiverEmailId()).isEqualTo(emailDTO.getReceiverEmailId());
        assertThat(savedEmailDTO.getSubject()).isEqualTo(emailDTO.getSubject());
        assertThat(savedEmailDTO.getBody()).isEqualTo(emailDTO.getBody());
        assertThat(savedEmailDTO.getClientId()).isEqualTo(emailDTO.getClientId());
    }

    @Test
    void testSavePhone() {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setId(2L);
        phoneDTO.setOutgoingPhoneNumber("+1234567890");
        phoneDTO.setClientId(2001L);

        Phone phone = new Phone();
        phone.setId(2L);
        phone.setOutgoingPhoneNumber("+1234567890");
        phone.setClientId(2001L);

        Mockito.when(phoneRepository.save(any(Phone.class))).thenReturn(phone);

        // Act
        PhoneDTO savedPhoneDTO = emsService.savePhone(phoneDTO);

        // Assert
        assertThat(savedPhoneDTO).isNotNull();
        assertThat(savedPhoneDTO.getId()).isEqualTo(phoneDTO.getId());
        assertThat(savedPhoneDTO.getOutgoingPhoneNumber()).isEqualTo(phoneDTO.getOutgoingPhoneNumber());
        assertThat(savedPhoneDTO.getClientId()).isEqualTo(phoneDTO.getClientId());
    }

    @Test
    void testSaveSms() {
        // Arrange
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setId(3L);
        smsDTO.setPhone("+9876543210");
        smsDTO.setMessage("Test Message");
        smsDTO.setClientId(3001L);

        Sms sms = new Sms();
        sms.setId(3L);
        sms.setPhone("+9876543210");
        sms.setMessage("Test Message");
        sms.setClientId(3001L);

        Mockito.when(smsRepository.save(any(Sms.class))).thenReturn(sms);

        // Act
        SmsDTO savedSmsDTO = emsService.saveSms(smsDTO);

        // Assert
        assertThat(savedSmsDTO).isNotNull();
        assertThat(savedSmsDTO.getId()).isEqualTo(smsDTO.getId());
        assertThat(savedSmsDTO.getPhone()).isEqualTo(smsDTO.getPhone());
        assertThat(savedSmsDTO.getMessage()).isEqualTo(smsDTO.getMessage());
        assertThat(savedSmsDTO.getClientId()).isEqualTo(smsDTO.getClientId());
    }
}