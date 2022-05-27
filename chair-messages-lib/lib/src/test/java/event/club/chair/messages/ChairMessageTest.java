package event.club.chair.messages;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Adding a simple test as it seems impolite not too.
 */
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
}
