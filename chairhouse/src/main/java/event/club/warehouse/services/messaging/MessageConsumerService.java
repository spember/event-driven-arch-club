package event.club.warehouse.services.messaging;

import event.club.warehouse.services.InternalNotificationSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple implementation of an Observer pattern, this service listens for messages to come in on one or more kafka
 * topics via Spring's KafkaListener.
 *
 * Topics should be setup using {@link NewTopic} beans (e.g. in configuration).
 */
@Service
public class MessageConsumerService {

    private static Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    private final Map<String, List<InternalNotificationSubscriber<String>>> registeredSubscribers = new HashMap<>();

    @KafkaListener(topics = Topics.CHAIRS) // groupId specified in ConsumerConfiguration
    public void listenForChairUpdates(String message) {
        // once the Kafka client retrieves a new message, this service class is 'reacting' to the
        // incoming data and notifying any downstream Subscribers
        registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList())
                .forEach(subscriber -> subscriber.handle(message));
        // pro debugging tip: you're going to want to know if you're receiving messages you forgot
        // to wire up a Subscriber for
        if (registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList()).isEmpty()) {
            log.warn("Message received on topic {}, but there were no listeners", Topics.CHAIRS);
        }
    }

    public void register(String topic, InternalNotificationSubscriber<String> subscriber) {
        if (!registeredSubscribers.containsKey(topic)) {
            registeredSubscribers.put(topic, new ArrayList<>());
        }
        registeredSubscribers.get(topic).add(subscriber);
        log.info("Registered subscriber for topic {}", topic);
    }
}
