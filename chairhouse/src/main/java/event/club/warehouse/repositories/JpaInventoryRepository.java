package event.club.warehouse.repositories;

import event.club.warehouse.domain.Inventory;
import org.springframework.data.repository.CrudRepository;

public interface JpaInventoryRepository extends CrudRepository<Inventory, String> {
}
