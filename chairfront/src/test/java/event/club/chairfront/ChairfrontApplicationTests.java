package event.club.chairfront;

import event.club.chairfront.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

class ChairfrontApplicationTests extends BaseSpringIntegrationTest {

	@Test
	void contextLoads() {
		assertEquals(1, 1);
	}

	@Test
	void containersAreUp() {
		assert(postgreSQLContainer.isRunning());
	}

}
