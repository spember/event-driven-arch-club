package event.club.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdminApplication {

	private static final Logger log = LoggerFactory.getLogger(AdminApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
		log.info("Welcome to the World of Chairs");
	}

	/*
	todo:
	create sample Chair class and service
	create sample Jooq read and write into postgres, and test it

	duplicate for 'chairfront'

	run in k8s

	add a readme

	add kafka, and sample message consumer...

	 */
}
