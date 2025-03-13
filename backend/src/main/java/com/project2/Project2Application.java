package com.project2;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Project2Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Project2Application.class);
		Environment env = app.run(args).getEnvironment();
		System.out.println("🔥 Active profiles: " + Arrays.toString(env.getActiveProfiles()));
	}

}
