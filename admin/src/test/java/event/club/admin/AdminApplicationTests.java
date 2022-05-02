package event.club.admin;

import event.club.admin.support.BaseSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.assertEquals;

class AdminApplicationTests extends BaseSpringIntegrationTest {

	@Test
	void contextLoads() {
		assertEquals(1, 1);
	}

	@Test
	void containersAreUp() {
		assert(postgreSQLContainer.isRunning());
	}

}
