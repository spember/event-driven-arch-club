package event.club.warehouse;

import event.club.chair.messaging.messages.ChairCreated;
import event.club.warehouse.domain.Chair;
import event.club.warehouse.domain.Inventory;
import event.club.warehouse.repositories.JpaInventoryRepository;
import event.club.warehouse.services.ChairManagementService;
import event.club.warehouse.services.InventoryManagementService;
import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ChairControllerIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    InventoryManagementService inventoryManagementService;

    @Autowired
    ChairManagementService chairManagementService;

    @Autowired
    JpaInventoryRepository jpaInventoryRepository;

    @Test
    public void testChairsAreSeeded() {
        Chair[] chairs = this.restTemplate.getForObject(localUrl(), Chair[].class);
        assertTrue(chairs.length > 0);
        List<Inventory> myChairs = this.inventoryManagementService.loadAllForChair(
                UUID.fromString("25d5c2a7-8fdc-496d-a335-618d3c6e27b9")
        ).collect(Collectors.toList());
//        myChairs.forEach(inventory -> System.out.println("I have chair " + inventory.getSerial()));

        assertEquals(myChairs.size(), 150);
    }

    @Test
    public void testPricing() {
        UUID chairId = UUID.randomUUID();
        chairManagementService.handleCreate(new ChairCreated(
                chairId, 1, "TEST-CH-01", "Test Chair", "Blah"
        ));

        //String serial, UUID chairId, int version, Instant arrived, Instant purchased, Instant shipped
        Inventory check = new Inventory(
                "TCH00001",
                chairId,
                1,
                Instant.now(),
                null,null
        );
        jpaInventoryRepository.save(check);

        inventoryManagementService.recalculatePrice(check);

        check = jpaInventoryRepository.findById(check.getSerial()).get();

        assertNotNull(check);
        assertTrue(check.getCurrentPrice() > 0);
        int price = check.getCurrentPrice();

        inventoryManagementService.recalculatePrice(check);
        assertEquals(price, check.getCurrentPrice());
    }
}
