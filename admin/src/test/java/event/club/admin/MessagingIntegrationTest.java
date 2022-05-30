package event.club.admin;

import event.club.admin.services.messaging.MessageConsumerService;
import event.club.admin.services.messaging.MessageProducerService;
import event.club.admin.support.BaseSpringIntegrationTest;
import event.club.chair.messaging.Topics;
import event.club.chair.messaging.messages.ChairCreated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

public class MessagingIntegrationTest extends BaseSpringIntegrationTest {

    @Autowired
    private MessageConsumerService consumerService;

    @Autowired
    private MessageProducerService producerService;

    @Test
    void basicEventTest() throws InterruptedException {
        UUID newChairId = UUID.randomUUID();
        CountDownLatch latch = new CountDownLatch(1);
        // normally in an integration test, we'd set up a Consumer within the scope of the test class to assert
        // receiving of the messages that come out of the service. something like this:
        // http://cloudurable.com/blog/kafka-tutorial-kafka-consumer/index.html
        // however, for the sake of time - and that the service always has a no-op consumer for its own topics -
        // we'll just reuse that
        consumerService.register(Topics.CHAIRS, ChairCreated.class, value -> {
            latch.countDown();
            assertEquals(newChairId, value.getId());
        });

        ChairCreated created = new ChairCreated(
                newChairId,
                1,
                "MC-0101",
                "My chair",
                "This is a great chair"
        );

        producerService.emit(Topics.CHAIRS, created);
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());
    }
}
