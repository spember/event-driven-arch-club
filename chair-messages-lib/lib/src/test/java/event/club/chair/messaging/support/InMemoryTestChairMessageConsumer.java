package event.club.chair.messaging.support;

import com.fasterxml.jackson.databind.ObjectReader;
import event.club.chair.messaging.BaseChairMessageConsumer;
import event.club.chair.messaging.MessageTypeRegistry;

public class InMemoryTestChairMessageConsumer extends BaseChairMessageConsumer {

    public InMemoryTestChairMessageConsumer(ObjectReader objectReader, MessageTypeRegistry registry) {
        super(objectReader, registry);
    }
}
