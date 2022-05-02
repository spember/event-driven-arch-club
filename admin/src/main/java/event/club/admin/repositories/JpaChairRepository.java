package event.club.admin.repositories;

import event.club.admin.domain.Chair;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaChairRepository extends CrudRepository<Chair, UUID> {
    Optional<Chair> findById(UUID id);
}
