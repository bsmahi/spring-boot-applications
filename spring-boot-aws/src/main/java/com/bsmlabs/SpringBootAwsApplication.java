package com.bsmlabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootAwsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAwsApplication.class, args);
	}

}
