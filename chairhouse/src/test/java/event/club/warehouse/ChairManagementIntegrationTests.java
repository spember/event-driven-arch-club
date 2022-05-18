package event.club.warehouse;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.services.ChairManagementService;
import event.club.warehouse.support.BaseSpringIntegrationTest;
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
