package event.club.chairfront.support;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Because the @Container is reset between test classes, we need a method to set the env vars BEFORE the spring context starts.
 * There's currently no good way to do it that I know of with Junit.. @Before occurs before all the tests run, and @BeforeEach
 * occurs before each test but AFTER the spring context loads.
 *
 * This approach allows us to reset the local env vars on each test file execution with the new container instance,
 * utilizing env vars read by application.properties in the test env, but without messing with the official Spring
 * system props (e.g. SPRING_DATASOURCE_URL)
 */
public class SpringPostgresSqlContainer extends PostgreSQLContainer<SpringPostgresSqlContainer> {
    private static SpringPostgresSqlContainer container;

    private SpringPostgresSqlContainer() {
        super("postgres:14.2-alpine");
    }

    public static SpringPostgresSqlContainer getInstance() {
        if (container == null) {
            container = new SpringPostgresSqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.out.println("JDBC connection = " + container.getJdbcUrl());
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }

}
