package event.club.admin.repositories;

import event.club.admin.domain.Chair;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaChairRepository extends CrudRepository<Chair, UUID> {
    Optional<Chair> findById(UUID id);

    @Modifying
    @Query("update Chair c set c.version = ?2, c.name = ?3, c.description = ?4 where c.id=?1")
    void updateChairInfoById(UUID id, int version, String name, String description);
}
