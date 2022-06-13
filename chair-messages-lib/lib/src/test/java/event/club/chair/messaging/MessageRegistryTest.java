package event.club.chair.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.chair.messaging.support.InMemoryTestChairMessageProducer;
import event.club.chair.messaging.support.ObjectMapperParameterResolver;
import event.club.chair.messaging.support.RegistryResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ObjectMapperParameterResolver.class, RegistryResolver.class})
public class MessageRegistryTest {

    @Test
    void basicScanOfClassesInAPackage() {
           MessageTypeRegistry registry = new MessageTypeRegistry("event.club");
           assertEquals("chair-created", registry.getAliasForMessage(ChairCreated.class).get());
           assertEquals(ChairUpdated.class, registry.getMessageForAlias("chair-updated").get());
    }

    @Test
    void publisherShouldConvertAppropriately(ObjectMapper objectMapper, MessageTypeRegistry registry) throws JsonProcessingException {
        InMemoryTestChairMessageProducer producer = new InMemoryTestChairMessageProducer(objectMapper, null, registry);
        UUID chairId = UUID.randomUUID();
        Message<String> result = producer.convert(DomainTopics.CHAIRS, new ChairCreated(chairId, 1, "sk-012", "Test Chair", "Fancy Chair"));
        assertEquals(DomainTopics.CHAIRS, result.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals("chair-created", result.getHeaders().get(MessageHeaders.CLASS));

        assertNotNull(result.getPayload());
    }
}
