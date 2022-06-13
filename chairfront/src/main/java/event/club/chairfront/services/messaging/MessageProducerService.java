package event.club.chairfront.services.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.BaseChairMessageProducer;
import event.club.chair.messaging.MessageTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Used to emit messages onto our bus (which is Kakfa).
 */
@Service
public class MessageProducerService extends BaseChairMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    @Autowired
    public MessageProducerService(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate, MessageTypeRegistry registry) {
        super(objectMapper, kafkaTemplate, registry);
    }
}
