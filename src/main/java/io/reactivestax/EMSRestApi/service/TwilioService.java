package io.reactivestax.EMSRestApi.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public String sendSms(String toPhoneNumber, String messageBody) {
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber), // To phone number
                new PhoneNumber(twilioPhoneNumber), // From Twilio number
                messageBody // Message body
        ).create();

        return message.getSid(); // Return message SID
    }
}
