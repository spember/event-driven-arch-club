package event.club.chair.messaging.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.BaseChairMessageProducer;
import event.club.chair.messaging.MessageTypeRegistry;
import org.springframework.kafka.core.KafkaTemplate;

public class InMemoryTestChairMessageProducer extends BaseChairMessageProducer {
    public InMemoryTestChairMessageProducer(ObjectMapper objectMapper,
                                            KafkaTemplate<String, String> kafkaTemplate,
                                            MessageTypeRegistry registry
    ) {
        super(objectMapper, kafkaTemplate, registry);
    }
}
