package event.club.warehouse.services.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Used to emit messages onto our bus (which is Kakfa).
 */
@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void emit(String topic, String payload) {
        log.info("Sending payload of '{}' to topic '{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}
