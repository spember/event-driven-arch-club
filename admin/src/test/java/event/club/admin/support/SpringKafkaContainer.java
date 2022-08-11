package event.club.admin.support;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

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
        if (!container.isRunning()) {
            super.start();
        }
        log.info("Test Kafka Container started with bootstrap of {} ", container.getBootstrapServers());
        System.setProperty("KAFKA_BROKER", container.getBootstrapServers());
    }

    @Override
    public void stop() {
        //
    }
}
