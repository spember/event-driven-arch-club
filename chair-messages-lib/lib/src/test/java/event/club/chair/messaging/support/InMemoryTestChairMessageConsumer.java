package event.club.chair.messaging.support;

import com.fasterxml.jackson.databind.ObjectReader;
import event.club.chair.messaging.BaseChairMessageConsumer;

public class InMemoryTestChairMessageConsumer extends BaseChairMessageConsumer {

    public InMemoryTestChairMessageConsumer(ObjectReader objectReader) {
        super(objectReader);
    }
}
