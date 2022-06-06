package event.club.warehouse.repositories;

import event.club.warehouse.domain.Inventory;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.UUID;

public interface JpaInventoryRepository extends CrudRepository<Inventory, String> {

    Collection<InventorySerialsOnly> findByChairId(UUID chairId);
}
