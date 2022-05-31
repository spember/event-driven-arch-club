package event.club.chairfront.services.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Used to emit messages onto our bus (which is Kakfa).
 */
@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public MessageProducerService(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T> void  emit(String topic, T data)  {
        try {
            Message message = MessageBuilder.withPayload(objectMapper.writer().writeValueAsString(data))
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(Topics.HEADER, data.getClass().getName())
                    .build();
            kafkaTemplate.send(message);
        } catch(JsonProcessingException e) {
            log.error("Could not write payload as json", e);
        }
    }
}
