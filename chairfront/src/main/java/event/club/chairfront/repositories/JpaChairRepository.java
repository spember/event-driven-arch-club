package event.club.chairfront.repositories;

import event.club.chairfront.domain.Chair;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaChairRepository extends CrudRepository<Chair, UUID> {
    Optional<Chair> findById(UUID id);

    List<Chair> findAllByUnitsOnHandGreaterThan(int unitsOnHand);

    Optional<Chair> findByIdAndUnitsOnHandGreaterThan(UUID id, int unitsOnHand);
}
