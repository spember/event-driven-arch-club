package event.club.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarehouseApplication {

	private static final Logger log = LoggerFactory.getLogger(WarehouseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WarehouseApplication.class, args);
		log.info("ChairHouse is no ready to track inventory!");
	}
}
