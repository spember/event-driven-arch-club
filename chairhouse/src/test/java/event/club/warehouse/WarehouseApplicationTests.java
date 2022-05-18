package event.club.warehouse;

import event.club.warehouse.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

class WarehouseApplicationTests extends BaseSpringIntegrationTest {

	@Test
	void contextLoads() {
		assertEquals(1, 1);
	}

	@Test
	void containersAreUp() {

		assert(postgreSQLContainer.isRunning());
		assert(springKafkaContainer.isRunning());
	}

}
