package event.club.chairfront.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * A wrapper around Kafka TestContainer, to easily inject env vars at startup.
 */
public class SpringKafkaContainer extends KafkaContainer {

    private static final Logger log = LoggerFactory.getLogger(SpringKafkaContainer.class);

    private static SpringKafkaContainer container;

    private SpringKafkaContainer() {
        super (DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
    }

    public static SpringKafkaContainer getInstance() {
        if (container == null) {
            container = new SpringKafkaContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        log.info("Test Kafka Container startd with bootstrap of {} ", container.getBootstrapServers());
        System.setProperty("KAFKA_BROKER", container.getBootstrapServers());
    }

    @Override
    public void stop() {
        // no stop!
    }
}
