package event.club.admin.services.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import event.club.admin.services.InternalNotificationSubscriber;
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
    // in the last lesson, this was just a container for topics -> subscribers which always receive Strings
    // now that we want to start casting to concrete classes, this Map adds another layer in which it checks for
    // class types.
    // This ensures the type-safety of our Subscribers.
    private final Map<String, List<ClassSubscriberPair<?>>> registeredSubscribers = new HashMap<>();

    private final ObjectReader objectReader;

    @Autowired
    public MessageConsumerService(ObjectMapper objectMapper) {
        this.objectReader = objectMapper.reader();
    }

    @KafkaListener(topics = Topics.CHAIRS)
    public void listenForChairUpdates(@Header(Topics.HEADER) String clazz, @Payload String message) {
        if (clazz == null || clazz.isEmpty()) {
            log.error("Received a message with no Message class in Header");
            return;
        }
        log.info("Received: {}", message);
        try {
            Object hydratedMessage = objectReader.readValue(message, this.getClass().getClassLoader().loadClass(clazz));
            log.info("Received message {}", hydratedMessage);
            registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList())
                    .forEach(handlerPair -> handlerPair.handleIfMatch(hydratedMessage));
        } catch (IOException e) {
            log.error("Failed to process message", e);
        } catch (ClassNotFoundException e) {
            log.error("Could not cast to class", e);
        }

        if (registeredSubscribers.getOrDefault(Topics.CHAIRS, Collections.emptyList()).isEmpty()) {
            log.warn("Message received on topic {}, but there were no listeners", Topics.CHAIRS);
        }
    }

    public <T> void register(String topic, Class<T> incomingMessageClass,  InternalNotificationSubscriber<T> subscriber) {
        if (!registeredSubscribers.containsKey(topic)) {
            registeredSubscribers.put(topic, new ArrayList<>());
        }
        registeredSubscribers.get(topic).add(new ClassSubscriberPair<>(incomingMessageClass, subscriber));
        log.info("Registered subscriber for topic {} and message class {}", topic, incomingMessageClass);
    }

    /**
     * Internal Pair wrapper class to bundle the expected type of Message received with the actual Internal Subscriber.
     *
     * @param <T> The Class of the incoming Message
     */
    private class ClassSubscriberPair<T> {
        private Class<T> clazz;

        private InternalNotificationSubscriber<T> subscriber;

        public ClassSubscriberPair(Class<T> clazz, InternalNotificationSubscriber<T> subscriber) {
            this.clazz = clazz;
            this.subscriber = subscriber;
        }

        public <I> void handleIfMatch(I incomingData) {
            if (incomingData.getClass() == this.clazz) {
                // unchecked cast, but we just checked it
                this.subscriber.handle((T)incomingData);
            }
        }
    }

}
