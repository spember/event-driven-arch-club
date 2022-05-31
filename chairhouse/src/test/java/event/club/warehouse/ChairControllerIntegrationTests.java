package event.club.warehouse;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.repositories.JpaChairRepository;
import event.club.warehouse.services.ChairManagementService;
import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;


public class ChairControllerIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    private JpaChairRepository repository;

    @Autowired
    private ChairManagementService chairManagementService;

    @BeforeEach
    public void clear() {
        repository.deleteAll();
    }

    @Test
    public void testChairsAreEmpty() {
        assertEquals(this.restTemplate.getForObject(localUrl(), Chair[].class).length, 0);
    }




}
