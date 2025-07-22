package com.teleport.trackingnumbergenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "com.teleport.trackingnumbergenerator.repository.postgresql"
)
@EnableMongoRepositories(
		basePackages = "com.teleport.trackingnumbergenerator.repository.mongodb"
)
public class TrackingNumberGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingNumberGeneratorApplication.class, args);
	}

}
