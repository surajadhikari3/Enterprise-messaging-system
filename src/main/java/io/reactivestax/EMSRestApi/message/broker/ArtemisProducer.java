package io.reactivestax.EMSRestApi.message.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArtemisProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(String queueName, String message, String type) {
        jmsTemplate.convertAndSend(queueName, type + ","+ message);
        log.info("Message sent to queue: {} | Content: {} ] messageType: {}", queueName, message, type);
    }
}
