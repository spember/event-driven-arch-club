package event.club.warehouse;

import event.club.warehouse.domain.Chair;
import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;


public class ChairControllerIntegrationTests extends BaseSpringIntegrationTest {

    @Test
    public void testChairsAreSeeded() {
        assertEquals(this.restTemplate.getForObject(localUrl(), Chair[].class).length, 1);
    }




}
