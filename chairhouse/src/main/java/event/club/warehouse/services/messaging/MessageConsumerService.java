package event.club.warehouse.services.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import event.club.chair.messaging.BaseChairMessageConsumer;
import event.club.chair.messaging.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * A simple implementation of an Observer pattern, this service listens for messages to come in on one or more kafka
 * topics via Spring's KafkaListener.
 *
 * Topics should be setup using {@link NewTopic} beans (e.g. in configuration).
 */
@Service
public class MessageConsumerService extends BaseChairMessageConsumer {

    private static Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    @Autowired
    public MessageConsumerService(ObjectMapper objectMapper) {
        super(objectMapper.reader());
    }

    @KafkaListener(topics = event.club.chair.messaging.Topics.CHAIRS)
    public void listenForChairUpdates(@Header(Topics.HEADER) String clazz, @Payload String message) {
        if (clazz == null || clazz.isEmpty()) {
            log.error("Received a message with no Message class in Header");
            return;
        }
        this.handleDelivery(clazz, message);
    }
}
