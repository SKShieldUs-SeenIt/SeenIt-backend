package com.basic.miniPjt5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.basic.miniPjt5")
public class MiniPjt5Application {

	public static void main(String[] args) {
		SpringApplication.run(MiniPjt5Application.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Application started successfully!");
	}
}

