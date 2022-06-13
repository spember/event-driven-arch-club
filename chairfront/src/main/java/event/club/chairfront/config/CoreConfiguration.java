package event.club.chairfront.config;

import event.club.chair.messaging.MessageTypeRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

    @Bean
    public MessageTypeRegistry provideMessageTypeRegistry() {
        return new MessageTypeRegistry("event.club");
    }
}
