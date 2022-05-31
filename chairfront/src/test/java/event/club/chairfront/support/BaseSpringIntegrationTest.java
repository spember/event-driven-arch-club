package event.club.chairfront.support;

import event.club.chairfront.ChairfrontApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Support class intended to be used by other integration tests in this package. Sets up various configuration and links
 * to TestContainers.
 */
@Testcontainers
@SpringBootTest(classes = ChairfrontApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSpringIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(BaseSpringIntegrationTest.class);

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Container
    public static SpringPostgresSqlContainer postgreSQLContainer = SpringPostgresSqlContainer.getInstance()
            .withDatabaseName("chairfront")
            .withUsername("chairUser")
            .withPassword("abc123")
            .waitingFor(Wait.forListeningPort());

    @Container
    public static SpringKafkaContainer springKafkaContainer = SpringKafkaContainer.getInstance();

    protected String registrationUrl() {
        return "http://localhost:" + port+"/register";
    }
    protected String catalogUrl() {
        return "http://localhost:" + port+"/catalog";
    }
}
