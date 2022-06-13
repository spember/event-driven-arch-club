package event.club.warehouse;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.domain.Inventory;
import event.club.warehouse.repositories.JpaChairRepository;
import event.club.warehouse.repositories.JpaInventoryRepository;
import event.club.warehouse.services.ChairManagementService;
import event.club.warehouse.services.InventoryManagementService;
import event.club.warehouse.services.messaging.InternalTopics;
import event.club.warehouse.services.messaging.MessageConsumerService;
import event.club.warehouse.services.messaging.MessageProducerService;
import event.club.warehouse.services.messaging.messages.RecalculateIndividualPriceCommand;
import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PricingJobIntegrationTest extends BaseSpringIntegrationTest {

    @Autowired
    InventoryManagementService inventoryManagementService;

    @Autowired
    ChairManagementService chairManagementService;

    @Autowired
    JpaInventoryRepository jpaInventoryRepository;

    @Autowired
    JpaChairRepository jpaChairRepository;

    @Autowired
    private MessageConsumerService consumerService;

    @Autowired
    private MessageProducerService producerService;

    @Test
    void jobShouldFireNumberOfTimesPerInventory() throws InterruptedException {
        // setup chair
        UUID chairId = UUID.randomUUID();
        jpaChairRepository.save(new Chair(chairId, 1, "FC-01", "Fancy Chair"));
        // setup 1 inventory
        jpaInventoryRepository.save(new Inventory("fc000001", chairId, 1, Instant.now(), null, null));
        // kick off message

        CountDownLatch latch = new CountDownLatch(1);
        consumerService.register(InternalTopics.WAREHOUSE_WORK, RecalculateIndividualPriceCommand.class, recalculateIndividualPriceCommand -> {
            latch.countDown();
        });
        restTemplate.postForLocation("/inventory/recalculate/" + chairId, "");
        latch.await(5000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        Inventory inventory = jpaInventoryRepository.findById("fc000001").get();
        assertEquals(chairId, inventory.getChairId());
        assertTrue(inventory.getCurrentPrice() > 0);
    }
}
