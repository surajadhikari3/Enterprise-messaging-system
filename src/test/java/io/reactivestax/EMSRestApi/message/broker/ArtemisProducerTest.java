package io.reactivestax.EMSRestApi.message.broker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ArtemisProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private ArtemisProducer artemisProducer;

    @Test
    void testSendMessage() {
        // Arrange
        String queueName = "testQueue";
        String message = "testMessage";
        String type = "testType";

        artemisProducer.sendMessage(queueName, message, type);

        verify(jmsTemplate, times(1)).convertAndSend(queueName, type + "," + message);
    }
}
