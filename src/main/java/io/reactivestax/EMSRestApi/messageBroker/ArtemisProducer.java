package io.reactivestax.EMSRestApi.messageBroker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ArtemisProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(String queueName, String message, String type) {
        jmsTemplate.convertAndSend(queueName, type +","+ message);
        System.out.println("Message sent to queue: " + queueName + " | Content: " + message);
    }
}
