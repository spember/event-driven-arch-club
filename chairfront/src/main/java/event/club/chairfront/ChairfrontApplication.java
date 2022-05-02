package event.club.chairfront;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChairfrontApplication {

	private static final Logger log = LoggerFactory.getLogger(ChairfrontApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ChairfrontApplication.class, args);
		log.info("Welcome to the World of Chairs! Please buy some chairs!");
	}
}
