package io.reactivestax.EMSRestApi.messageBroker;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ArtemisConsumer {

    private String lastReceivedMessage;

    @JmsListener(destination = "ems-queue")
    public void consumeMessage(String message) {
        System.out.println("Message received from queue: " + message);
        this.lastReceivedMessage = message; // Store the message for testing
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }
}
