package event.club.warehouse.config;

import event.club.chair.messaging.MessageTypeRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

    @Bean
    public MessageTypeRegistry provideRegistry() {
        return new MessageTypeRegistry("event.club");
    }
}
