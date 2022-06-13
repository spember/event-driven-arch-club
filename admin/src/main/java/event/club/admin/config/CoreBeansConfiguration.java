package event.club.admin.config;

import event.club.chair.messaging.MessageTypeRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreBeansConfiguration {

    @Bean
    public MessageTypeRegistry provideRegistry() {
        return new MessageTypeRegistry("event.club");
    }
}
