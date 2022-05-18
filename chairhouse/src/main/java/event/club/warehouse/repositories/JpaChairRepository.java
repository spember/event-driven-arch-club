package event.club.warehouse.repositories;

import event.club.warehouse.domain.Chair;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaChairRepository extends CrudRepository<Chair, UUID> {
    Optional<Chair> findById(UUID id);
}
