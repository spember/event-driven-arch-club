package event.club.admin.orders;

import event.club.admin.orders.domain.OrderTracker;
import event.club.admin.orders.domain.Stage;
import event.club.admin.orders.domain.StageStatus;
import event.club.admin.orders.repositories.JpaOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderProcessingService {

    private final JpaOrderRepository orderRepository;

    @Autowired
    public OrderProcessingService(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void initialize(UUID orderId, String customerId, String productSerial) {
        OrderTracker order = new OrderTracker(orderId, customerId, productSerial);
        this.orderRepository.save(order);
    }

    public Optional<OrderTracker> load(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public void markStageComplete(OrderTracker order, Stage stage) {
        if (stage == Stage.ITEM_RESERVED) {
            order.setItemReserved(StageStatus.COMPLETED);
        } else if (stage == Stage.PAYMENT_PROCESSED) {
            order.setPaymentProcessed(StageStatus.COMPLETED);
        } else if (stage == Stage.CONFIRMATION_SENT) {
            order.setConfirmationSent(StageStatus.COMPLETED);
        } else if (stage == Stage.ITEM_SHIPPED) {
            order.setItemShipped(StageStatus.COMPLETED);
        } else {
            throw new RuntimeException("Unknown stage observed");
        }
        orderRepository.save(order);
    }

    public void markStageComplete(UUID orderId, Stage stage) {
        Optional<OrderTracker> maybeOrder = load(orderId);
        if (maybeOrder.isEmpty()) {
            throw new RuntimeException("Unknown Order");
        }
        markStageComplete(maybeOrder.get(), stage);
    }

    public void complete(OrderTracker order) {
        order.setTimeCompleted(Instant.now());
        orderRepository.save(order);
    }

}
