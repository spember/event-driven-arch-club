package event.club.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolsConfiguration {

    /**
     * Configures a ThreadPool for IO work.
     *
     * @return an ExecutorService for async work to utilize
     */
    @Bean
    public ExecutorService getExecutor() {
        return Executors.newFixedThreadPool(2);
    }
}
