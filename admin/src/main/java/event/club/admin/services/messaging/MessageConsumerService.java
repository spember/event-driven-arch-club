package event.club.admin.services.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.admin.services.InternalNotificationSubscriber;
import event.club.chair.messages.ChairCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


import java.io.IOException;
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

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = Topics.CHAIRS)
    public void listenForChairUpdates(@Header(Topics.HEADER) String clazz, @Payload String message) {
        log.info("Received: {}", message);
        try {
            log.info("received {}", objectMapper.reader().readValue(message, this.getClass().getClassLoader().loadClass(clazz)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList())
                .forEach(subscriber -> subscriber.handle(message));
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
