package event.club.admin;

import event.club.admin.support.BaseSpringIntegrationTest;
import event.club.admin.orders.OrderProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTrackingIntegrationTest extends BaseSpringIntegrationTest {

    @Autowired
    OrderProcessingService orderProcessingService;

    @Test
    void ordersShouldInitiateWithNothingTracked() {
        UUID trackingId = orderProcessingService.initialize("customer123", "CH01-WXYZ");
        assertNotNull(trackingId);
    }
}
