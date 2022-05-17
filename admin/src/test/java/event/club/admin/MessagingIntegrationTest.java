package event.club.admin;

import event.club.admin.services.messaging.MessageConsumerService;
import event.club.admin.services.messaging.MessageProducerService;
import event.club.admin.services.messaging.MessageSubscriber;
import event.club.admin.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

        consumerService.register("chair-updates", new MessageSubscriber<String>() {
            @Override
            public void handle(String value) {
                latch.countDown();
            }
        });

        producerService.emit("chair-updates", "This is a test");
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());
    }
}
