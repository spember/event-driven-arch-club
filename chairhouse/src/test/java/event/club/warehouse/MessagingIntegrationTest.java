package event.club.warehouse;

import event.club.chair.messaging.DomainTopics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.warehouse.domain.Chair;
import event.club.warehouse.services.messaging.MessageConsumerService;
import event.club.warehouse.services.messaging.MessageProducerService;

import event.club.warehouse.support.BaseSpringIntegrationTest;
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
    void handleSimpleCreate() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        consumerService.register(DomainTopics.CHAIRS, ChairCreated.class, value -> latch.countDown());
        UUID chairId = UUID.randomUUID();
        producerService.emit(DomainTopics.CHAIRS, new ChairCreated(
                chairId,
                1,
                "CH-0123",
                "My first Chair",
                "Chairs are life"
        ));
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        Chair newChair = this.restTemplate.getForObject(localUrl() +"/" + chairId, Chair.class);
        assertEquals(chairId, newChair.getId());
        assertEquals(1, newChair.getVersion());
        assertEquals("My first Chair", newChair.getName());
    }

    @Test
    void updatingShouldBeGreat() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        consumerService.register(DomainTopics.CHAIRS, ChairCreated.class, value -> latch.countDown());
        consumerService.register(DomainTopics.CHAIRS, ChairUpdated.class, value -> latch.countDown());

        UUID chairId = UUID.randomUUID();
        producerService.emit(DomainTopics.CHAIRS, new ChairCreated(
                chairId,
                1,
                "CH-9999",
                "My first Chair",
                "Chairs are life"
        ));

        producerService.emit(DomainTopics.CHAIRS, new ChairUpdated(
                chairId,
                2,
                "CH-9999",
                "My First Chair",
                "Chairs are life"
        ));

        producerService.emit(DomainTopics.CHAIRS, new ChairCreated(
                chairId,
                3,
                "CH-9999",
                "My First Chair",
                "It is a very excellent chair" // this one would be .. ignored
        ));

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        Chair newChair = this.restTemplate.getForObject(localUrl() +"/" + chairId, Chair.class);
        assertEquals(chairId, newChair.getId());
        assertEquals(3, newChair.getVersion());
    }
}
