package event.club.admin;

import event.club.admin.orders.domain.OrderTracker;
import event.club.admin.orders.domain.Stage;
import event.club.admin.orders.domain.StageStatus;
import event.club.admin.support.BaseSpringIntegrationTest;
import event.club.admin.orders.OrderProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTrackingIntegrationTest extends BaseSpringIntegrationTest {

    @Autowired
    OrderProcessingService orderProcessingService;

    @Test
    void ordersShouldInitiateWithNothingTracked() {
        UUID orderId = UUID.randomUUID();
        orderProcessingService.initialize(orderId, "customer123", "CH01-WXYZ");

        Optional<OrderTracker> maybeOrder =  orderProcessingService.load(orderId);
        assertTrue(maybeOrder.isPresent());
        OrderTracker order = maybeOrder.get();

        assertEquals("customer123", order.getCustomerId());
        assertEquals("CH01-WXYZ", order.getProductSerial());
        assertEquals(StageStatus.NOT_OBSERVED, order.getItemReserved());
        assertEquals(StageStatus.NOT_OBSERVED, order.getConfirmationSent());
        assertEquals(StageStatus.NOT_OBSERVED, order.getPaymentProcessed());
        assertEquals(StageStatus.NOT_OBSERVED, order.getItemShipped());
    }

    @Test
    void ordersShouldUpdateStates() {
        UUID trackingId = UUID.randomUUID();
        orderProcessingService.initialize(trackingId,"customer123", "CH00002");
        orderProcessingService.markStageComplete(trackingId, Stage.ITEM_RESERVED);
        orderProcessingService.markStageComplete(trackingId, Stage.CONFIRMATION_SENT);
        Optional<OrderTracker> maybeOrder = orderProcessingService.load(trackingId);

        assertTrue(maybeOrder.isPresent());
        assertEquals(StageStatus.COMPLETED, maybeOrder.get().getItemReserved());
        assertEquals(StageStatus.COMPLETED, maybeOrder.get().getConfirmationSent());
        assertEquals(StageStatus.NOT_OBSERVED, maybeOrder.get().getPaymentProcessed());
    }
}
