package event.club.warehouse.services.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.BaseChairMessageConsumer;
import event.club.chair.messaging.DomainTopics;
import event.club.chair.messaging.MessageHeaders;
import event.club.chair.messaging.MessageTypeRegistry;
import org.apache.kafka.clients.admin.NewTopic;
import org.jooq.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public MessageConsumerService(ObjectMapper objectMapper, MessageTypeRegistry messageTypeRegistry) {
        super(objectMapper.reader(), messageTypeRegistry);
    }

    @KafkaListener(topics = DomainTopics.CHAIRS)
    public void listenForChairUpdates(@Header(MessageHeaders.CLASS) String alias, @Payload String message) {
        // the handle function now checks for empty alias strings
        this.handleDelivery(DomainTopics.CHAIRS, alias, message);
    }

    @KafkaListener(topics = InternalTopics.WAREHOUSE_WORK)
    public void listenForWorkUpdates(@Header(MessageHeaders.CLASS) String alias, @Payload String message) {
        this.handleDelivery(InternalTopics.WAREHOUSE_WORK, alias, message);
    }
}
