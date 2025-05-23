package com.example.bai1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.bai1.Model")
@EnableJpaRepositories(basePackages = "com.example.bai1.Repository")
public class Bai1Application {

	public static void main(String[] args) {
		SpringApplication.run(Bai1Application.class, args);
	}

}
