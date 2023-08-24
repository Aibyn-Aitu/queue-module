package kz.aitu.abiturqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbiturQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbiturQueueApplication.class, args);
	}

}
