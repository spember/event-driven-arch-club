package event.club.chair.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.chair.messaging.messages.DomainMessage;
import event.club.chair.messaging.support.InMemoryTestChairMessageConsumer;
import event.club.chair.messaging.support.ObjectMapperParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Adding a simple test as it seems impolite not too.
 */
@ExtendWith(ObjectMapperParameterResolver.class)
public class ChairMessageTest {

    @Test
    void shouldBeAbleToCreateChairMessages() {
        UUID myId = UUID.randomUUID();
        ChairUpdated updated = new ChairUpdated();
        updated.setId(myId);
        updated.setVersion(2);
        updated.setName("My Chair");
        updated.setSku("MC-01");
        updated.setDescription("This is a test");

        assertEquals(myId, updated.getId());
    }

    @Test
    void registerAndParseShouldWork(ObjectMapper objectMapper) throws JsonProcessingException {
        InMemoryTestChairMessageConsumer consumer = new InMemoryTestChairMessageConsumer(objectMapper.reader());
        List<DomainMessage> captured = new ArrayList<>();
        consumer.register("foo-bar", ChairCreated.class, captured::add);

        UUID id = UUID.randomUUID();
        Optional<ChairCreated> parsed = consumer.parse(ChairCreated.class.getName(), objectMapper.writeValueAsString(
                new ChairCreated(id, 1, "TC-01", "Test Chair", "This is a test")
        ));

        assertFalse(parsed.isEmpty());
        assertEquals(id, parsed.get().getId());
        assertEquals("TC-01", parsed.get().getSku());
    }
}
