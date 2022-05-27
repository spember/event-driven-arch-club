package event.club.admin;

import event.club.admin.services.messaging.MessageConsumerService;
import event.club.admin.services.messaging.MessageProducerService;
import event.club.admin.services.InternalNotificationSubscriber;
import event.club.admin.services.messaging.Topics;
import event.club.admin.support.BaseSpringIntegrationTest;
import event.club.chair.messages.ChairCreated;
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
    void noMatchingIdShouldReturnNull() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        consumerService.register(Topics.CHAIRS, new InternalNotificationSubscriber<String>() {
            @Override
            public void handle(String value) {
                latch.countDown();
            }
        });

        ChairCreated created = new ChairCreated(
                UUID.randomUUID(),
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
