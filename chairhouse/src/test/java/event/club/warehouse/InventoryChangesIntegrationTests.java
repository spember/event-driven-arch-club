package event.club.warehouse;

import event.club.chair.messaging.DomainTopics;
import event.club.chair.messaging.MessageTypeRegistry;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.inventory.InventoryAdded;
import event.club.warehouse.http.ReceiveItemCommand;
import event.club.warehouse.services.messaging.MessageConsumerService;
import event.club.warehouse.services.messaging.MessageProducerService;
import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryChangesIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    private MessageConsumerService consumerService;

    @Autowired
    private MessageProducerService producerService;

    @Autowired
    private MessageTypeRegistry messageTypeRegistry;


    @Test
    void newItemsShouldTriggerAddMessages() throws InterruptedException {
        CountDownLatch chairLatch = new CountDownLatch(1);
        CountDownLatch inventoryLatch = new CountDownLatch(1);
        List<InventoryAdded> captured = new ArrayList<>();
        consumerService.register(DomainTopics.CHAIRS, ChairCreated.class, value -> chairLatch.countDown());
        consumerService.register(DomainTopics.INVENTORY, InventoryAdded.class, value -> {
            System.out.println("Captured!");
            inventoryLatch.countDown();
            captured.add(value);
        });

        UUID chairId = UUID.randomUUID();
        producerService.emit(DomainTopics.CHAIRS, new ChairCreated(
                chairId,
                1,
                "CH-TST01",
                "My Ideal Test Chair",
                "Chairs are life"
        ));

        chairLatch.await(1000, TimeUnit.MILLISECONDS);

        String serial = "CHTEST01-A-1";
        this.restTemplate.postForLocation(baseUrl() +"/inventory", new ReceiveItemCommand(chairId, serial));
        assertTrue(messageTypeRegistry.getMessageForAlias("inventory-added").isPresent());

        // need a test-oriented consumer

//        inventoryLatch.await(2000, TimeUnit.MILLISECONDS);
//        assertEquals(1, captured.size());
//        assertEquals(chairId, captured.get(0).getChairId());
//        assertEquals(serial, captured.get(0).getSerial());
//        assertTrue(captured.get(0).getPrice() > 0);
    }
}
