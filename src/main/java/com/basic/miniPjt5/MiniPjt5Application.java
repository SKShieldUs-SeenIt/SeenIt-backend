package com.basic.miniPjt5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.basic.miniPjt5")
public class MiniPjt5Application {

	public static void main(String[] args) {
		SpringApplication.run(MiniPjt5Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("=== Kakao 관련 Bean 목록 ===");
			Arrays.stream(ctx.getBeanDefinitionNames())
					.filter(name -> name.toLowerCase().contains("kakao"))
					.forEach(System.out::println);
		};
	}
}

