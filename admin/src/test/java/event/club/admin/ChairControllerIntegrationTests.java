package event.club.admin;

import event.club.admin.domain.Chair;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import event.club.admin.services.ChairManagementService;
import event.club.admin.services.InternalNotificationSubscriber;
import event.club.admin.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


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

    @Test
    public void creationShouldWorkAsync() throws InterruptedException {
        String sku = "CH-01-MA";
        String name = "My First Chair";
        String description = "This is one great chair!";

        CountDownLatch latch = new CountDownLatch(1); // expecting one notification;

        chairManagementService.register(value -> latch.countDown());

        Chair tested = this.restTemplate.postForObject(localUrl(), new UpdateChairCommand(
                sku,
                name,
                description
        ), Chair.class);
        // we received a response, but the countdown should not have gone down yet
        assertEquals(1, latch.getCount());
        assertEquals( 1, tested.getVersion());
        assertEquals(sku, tested.getSku());
        assertNotNull(tested.getId());

        latch.await(1500, TimeUnit.MILLISECONDS);
        Chair loaded = this.restTemplate.getForObject(localUrl()+"/"+tested.getId(), Chair.class);
        assertEquals(0, latch.getCount());
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
