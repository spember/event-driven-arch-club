package event.club.chairfront;

import event.club.chairfront.domain.Chair;
import event.club.chairfront.http.UpdateChairFromUpstreamCommand;
import event.club.chairfront.repositories.JpaChairRepository;
import event.club.chairfront.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


public class ChairfrontControllerIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    private JpaChairRepository repository;

    @BeforeEach
    public void clear() {
        repository.deleteAll();
    }

    @Test
    public void testChairsAreEmpty() {
        assertEquals(this.restTemplate.getForObject(catalogUrl(), Chair[].class).length, 0);
    }

    @Test
    public void creationShouldWork() {
        String sku = "CH-01-MA";
        String name = "My First Chair";
        String description = "This is one great chair!";

        Chair tested = this.restTemplate.postForObject(registrationUrl(), new UpdateChairFromUpstreamCommand(
                sku,
                name,
                description
        ), Chair.class);

        assertEquals(tested.getVersion(), 1);
        assertEquals(tested.getSku(), sku);
        assertNotNull(tested.getId());

        Chair loaded = this.restTemplate.getForObject(catalogUrl()+"/"+tested.getId(), Chair.class);
        assertNotNull(loaded);
        assertEquals(loaded.getVersion(), 1);
        assertEquals(loaded.getSku(), sku);
        assertEquals(loaded.getName(), name);
        assertEquals(loaded.getDescription(), description);
        assertEquals(loaded.getUnitsOnHand(), 0);

    }

    @Test
    public void onlyListChairsWithInventory() {
        String sku = "RK-01";
        String name = "Classic Rocker";
        String description = "This is one great chair!";

        Chair tested = this.restTemplate.postForObject(registrationUrl(), new UpdateChairFromUpstreamCommand(
                sku,
                name,
                description
        ), Chair.class);

        assertEquals(tested.getVersion(), 1);

        assertEquals(this.restTemplate.getForObject(catalogUrl()+"?includeAll=false", Chair[].class).length, 0);

        assertEquals(this.restTemplate.getForObject(catalogUrl()+"?includeAll=true", Chair[].class).length, 1);
    }

    private String registrationUrl() {
        return "http://localhost:" + port+"/register";
    }
    private String catalogUrl() {
        return "http://localhost:" + port+"/catalog";
    }

}
