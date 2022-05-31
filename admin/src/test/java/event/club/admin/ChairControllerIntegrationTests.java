package event.club.admin;

import event.club.admin.domain.Chair;
import event.club.admin.http.CreateChairCommand;
import event.club.admin.http.UpdateChairCommand;
import event.club.admin.repositories.JpaChairRepository;
import event.club.admin.services.ChairManagementService;
import event.club.admin.services.messaging.MessageConsumerService;
import event.club.admin.support.BaseSpringIntegrationTest;
import event.club.chair.messaging.Topics;
import event.club.chair.messaging.messages.ChairCreated;
import event.club.chair.messaging.messages.ChairUpdated;
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

    @Autowired
    private MessageConsumerService messageConsumerService;

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
        messageConsumerService.register(Topics.CHAIRS, ChairCreated.class, event -> latch.countDown());

        Chair tested = this.restTemplate.postForObject(localUrl(), new CreateChairCommand(
                sku,
                name,
                description
        ), Chair.class);
        // we received a response, but the countdown should not have gone down yet
        assertEquals(1, latch.getCount());
        assertEquals( 1, tested.getVersion());
        assertEquals(sku, tested.getSku());
        assertNotNull(tested.getId());

        // now wait for the latch to countdown (for our async op to complete)
        latch.await(1500, TimeUnit.MILLISECONDS);
        Chair loaded = this.restTemplate.getForObject(localUrl()+"/"+tested.getId(), Chair.class);
        assertEquals(0, latch.getCount());
        assertNotNull(loaded);
        assertEquals(loaded.getVersion(), 1);
        assertEquals(loaded.getSku(), sku);
        assertEquals(loaded.getName(), name);
        assertEquals(loaded.getDescription(), description);

    }

    @Test
    public void updatingShouldWork() throws InterruptedException {
        String sku = "CF-M123";
        String name = "or othr chr";
        String description = "Nice, a chair";

        CountDownLatch latch = new CountDownLatch(2); // expecting one notification;

        messageConsumerService.register(Topics.CHAIRS, ChairUpdated.class, event -> latch.countDown());

        Chair tested = this.restTemplate.postForObject(localUrl(), new CreateChairCommand(
                sku,
                name,
                description
        ), Chair.class);

        assertEquals(2, latch.getCount());
        assertEquals( 1, tested.getVersion());


        this.restTemplate.put(localUrl()+"/"+tested.getId(), new UpdateChairCommand(
                tested.getId(),
                sku,
                "Our Other Chair",
                description
        ), Chair.class);

        this.restTemplate.put(localUrl()+"/"+tested.getId(), new UpdateChairCommand(
                tested.getId(),
                sku,
                "Our Other Chair",
                "This is a proper description"
        ), Chair.class);

        latch.await(1500, TimeUnit.MILLISECONDS);
        Chair loaded = this.restTemplate.getForObject(localUrl()+"/"+tested.getId(), Chair.class);
        assertEquals(3, loaded.getVersion());
        assertEquals("Our Other Chair", loaded.getName());
        assertEquals("This is a proper description", loaded.getDescription());
    }

    private String localUrl() {
        return "http://localhost:" + port+"/chairs";
    }


}
