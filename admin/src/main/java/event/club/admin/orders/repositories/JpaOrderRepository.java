package event.club.admin.orders.repositories;

import event.club.admin.orders.domain.OrderTracker;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends CrudRepository<OrderTracker, UUID> {

    Optional<OrderTracker> findById(UUID id);
}
