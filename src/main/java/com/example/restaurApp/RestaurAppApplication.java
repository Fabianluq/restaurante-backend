package com.example.restaurApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestaurAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurAppApplication.class, args);
	}

}
