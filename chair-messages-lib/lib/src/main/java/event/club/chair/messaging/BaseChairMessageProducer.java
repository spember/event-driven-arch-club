package event.club.chair.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.exceptions.NoAliasFoundException;
import event.club.chair.messaging.messages.DomainMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;

public abstract class BaseChairMessageProducer {

    protected static final Logger log = LoggerFactory.getLogger(BaseChairMessageProducer.class);

    protected final ObjectMapper objectMapper;
    protected final KafkaTemplate<String, String> kafkaTemplate;
    protected final MessageTypeRegistry registry;

    public BaseChairMessageProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate, MessageTypeRegistry registry) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.registry = registry;
    }


    public <DM extends DomainMessage> void emit(String topic, DM message)  {
        try {
            kafkaTemplate.send(convert(topic, message));
        } catch(JsonProcessingException e) {
            log.error("Could not write payload as json", e);
        } catch(NoAliasFoundException nfe) {
            log.error("Could not send message: ", nfe);
        }
    }

    public <DM extends DomainMessage> Message<String> convert(String topic, DM message)
            throws JsonProcessingException, NoAliasFoundException {
        Optional<String> maybeAlias = registry.getAliasForMessage(message.getClass());
        if (maybeAlias.isEmpty()) {
            throw new NoAliasFoundException("No Alias found in the registry for class " + message.getClass());
        }
        return MessageBuilder.withPayload(objectMapper.writer().writeValueAsString(message))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(MessageHeaders.CLASS, maybeAlias.get())
                // todo: API Version
                .build();
    }
}
