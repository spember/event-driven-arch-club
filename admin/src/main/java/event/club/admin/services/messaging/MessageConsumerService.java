package event.club.admin.services.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageConsumerService {

    private static Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    private final Map<String, List<MessageSubscriber<String>>> registeredSubscribers = new HashMap<>();

    @KafkaListener(topics = Topics.CHAIRS)
    public void listenGroupFoo(String message) {
        registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList())
                .forEach(subscriber -> subscriber.handle(message));
    }

    public void register(String topic, MessageSubscriber<String> subscriber) {
        if (!registeredSubscribers.containsKey(topic)) {
            registeredSubscribers.put(topic, new ArrayList<>());
        }
        registeredSubscribers.get(topic).add(subscriber);
        log.info("Registered subscriber for topic {}", topic);
    }
}
