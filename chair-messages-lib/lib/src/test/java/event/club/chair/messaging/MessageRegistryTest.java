package event.club.chair.messaging;

import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageRegistryTest {

    @Test
    void basicScanOfClassesInAPackage() {
           MessageTypeRegistry registry = new MessageTypeRegistry("event.club");
           assertEquals("chair-created", registry.getAliasForMessage(ChairCreated.class).get());
           assertEquals(ChairUpdated.class, registry.getMessageForAlias("chair-updated").get());

    }
}
