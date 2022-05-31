package event.club.chairfront;

import event.club.chair.messaging.Topics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
import event.club.chairfront.domain.Chair;
import event.club.chairfront.services.messaging.MessageConsumerService;
import event.club.chairfront.services.messaging.MessageProducerService;
import event.club.chairfront.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

public class MessageConsumptionIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    public MessageConsumerService consumerService;

    @Autowired
    public MessageProducerService producerService;

    @Test
    void updatingShouldBeGreat() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        consumerService.register(Topics.CHAIRS, ChairCreated.class, value -> latch.countDown());
        consumerService.register(Topics.CHAIRS, ChairUpdated.class, value -> latch.countDown());

        UUID chairId = UUID.randomUUID();
        producerService.emit(Topics.CHAIRS, new ChairCreated(
                chairId,
                1,
                "CH-9999",
                "My first Chair",
                "Chairs are life"
        ));

        producerService.emit(Topics.CHAIRS, new ChairUpdated(
                chairId,
                2,
                "CH-9999",
                "My First Chair",
                "Chairs are life"
        ));

        producerService.emit(Topics.CHAIRS, new ChairCreated(
                chairId,
                3,
                "CH-9999",
                "My First Chair",
                "It is a very excellent chair" // this one would be .. ignored
        ));

        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        Chair newChair = this.restTemplate.getForObject(catalogUrl() +"/" + chairId, Chair.class);
        assertEquals(chairId, newChair.getId());
        assertEquals(3, newChair.getVersion());
    }
}
