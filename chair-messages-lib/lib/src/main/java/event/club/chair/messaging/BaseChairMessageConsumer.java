package event.club.chair.messaging;


import com.fasterxml.jackson.databind.ObjectReader;
import event.club.chair.messaging.messages.DomainMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Intended to be used by other services as Beans in order to ensure that we consume messages in a common manner.
 */
public abstract class BaseChairMessageConsumer {

    protected final static Logger log = LoggerFactory.getLogger(BaseChairMessageConsumer.class);
    // in the last lesson, this was just a container for topics -> subscribers which always receive Strings
    // now that we want to start casting to concrete classes, this Map adds another layer in which it checks for
    // class types.
    // This ensures the type-safety of our Subscribers.
    protected final Map<String, List<ClassSubscriberPair<?>>> registeredSubscribers = new HashMap<>();

    protected final ObjectReader objectReader;

    protected final MessageTypeRegistry registry;

    public BaseChairMessageConsumer(ObjectReader objectReader, MessageTypeRegistry registry) {
        this.objectReader = objectReader;
        this.registry = registry;
    }

    /**
     * Register a Subscriber to be called when a particular Message Type is received on a particular topic.
     *
     * @param topic
     * @param incomingMessageClass
     * @param subscriber
     * @param <T>
     */
    public <T extends DomainMessage> void register(String topic, Class<T> incomingMessageClass,  InternalNotificationSubscriber<T> subscriber) {
        if (!registeredSubscribers.containsKey(topic)) {
            registeredSubscribers.put(topic, new ArrayList<>());
        }
        registeredSubscribers.get(topic).add(new ClassSubscriberPair<>(incomingMessageClass, subscriber));
        log.info("Registered subscriber for topic {} and message class {}", topic, incomingMessageClass);
    }

    protected <T extends DomainMessage> void handleDelivery(String messageKey, String message) {
        Optional<Class<? extends DomainMessage>> maybeMessageClass = registry.getMessageForAlias(messageKey);
        if (maybeMessageClass.isEmpty()) {
            log.error("Received message with unknown key of {}", messageKey);
            return;
        }
        Optional<T> hydratedMessage = parse(maybeMessageClass.get().getName(), message);
        if (hydratedMessage.isEmpty()) {
            log.warn("No message was parsed for {}", messageKey);
            return;
        }
        registeredSubscribers.getOrDefault(DomainTopics.CHAIRS, Collections.emptyList())
                .forEach(handlerPair -> handlerPair.handleIfMatch(hydratedMessage.get()));
        if (registeredSubscribers.getOrDefault(DomainTopics.CHAIRS, Collections.emptyList()).isEmpty()) {
            log.warn("Message received on topic {}, but there were no listeners", DomainTopics.CHAIRS);
        }
    }

    /**
     * Given some incoming message data and a String representing its internal class, this method attempts to
     * load the class by key and parse the data into that class.
     *
     * @param messageClassToLoad
     * @param message
     */
    protected <T extends DomainMessage> Optional<T> parse(String messageClassToLoad, String message) {
        // we use Optional to signal the fact that this thing may fail
        log.info("Received: {}", message);
        try {
            T hydratedMessage = (T) objectReader.readValue(
                    message,
                    this.getClass().getClassLoader().loadClass(messageClassToLoad));
            log.info("Received message {}", hydratedMessage);
            return Optional.of(hydratedMessage);

        } catch (IOException e) {
            log.error("Failed to process message", e);
        } catch (ClassNotFoundException e) {
            log.error("Could not cast to class", e);
        }
        return Optional.empty();

    }


    /**
     * Internal Pair wrapper class to bundle the expected type of Message received with the actual Internal Subscriber.
     *
     * @param <T> The Class of the incoming Message
     */
    protected class ClassSubscriberPair<T extends DomainMessage> {
        private final Class<T> clazz;

        private final InternalNotificationSubscriber<T> subscriber;

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
