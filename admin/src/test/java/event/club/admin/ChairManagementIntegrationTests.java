package event.club.admin;

import event.club.admin.domain.Chair;
import event.club.admin.repositories.JpaChairRepository;
import event.club.admin.services.ChairManagementService;
import event.club.admin.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;

public class ChairManagementIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    public ChairManagementService service;

    @Test
    void noMatchingIdShouldReturnNull() {
        Optional<Chair> maybeChair = service.get(UUID.randomUUID());
        assertTrue(maybeChair.isEmpty());
    }

}
