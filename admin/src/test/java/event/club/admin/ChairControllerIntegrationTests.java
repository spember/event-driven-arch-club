package event.club.admin;

import event.club.admin.domain.Chair;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import event.club.admin.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


public class ChairControllerIntegrationTests extends BaseSpringIntegrationTest {

    @Autowired
    private JpaChairRepository repository;

    @BeforeEach
    public void clear() {
        repository.deleteAll();
    }

    @Test
    public void testChairsAreEmpty() {
        assertEquals(this.restTemplate.getForObject(localUrl(), Chair[].class).length, 0);
    }

    @Test
    public void creationShouldWork() {
        String sku = "CH-01-MA";
        String name = "My First Chair";
        String description = "This is one great chair!";

        Chair tested = this.restTemplate.postForObject(localUrl(), new UpdateChairCommand(
                sku,
                name,
                description
        ), Chair.class);

        assertEquals(tested.getVersion(), 1);
        assertEquals(tested.getSku(), sku);
        assertNotNull(tested.getId());

        Chair loaded = this.restTemplate.getForObject(localUrl()+"/"+tested.getId(), Chair.class);
        assertNotNull(loaded);
        assertEquals(loaded.getVersion(), 1);
        assertEquals(loaded.getSku(), sku);
        assertEquals(loaded.getName(), name);
        assertEquals(loaded.getDescription(), description);

    }

    private String localUrl() {
        return "http://localhost:" + port+"/chairs";
    }


}
