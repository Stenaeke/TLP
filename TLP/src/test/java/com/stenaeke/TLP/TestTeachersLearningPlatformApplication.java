package com.stenaeke.TLP;

import org.springframework.boot.SpringApplication;

public class TestTeachersLearningPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(TeachersLearningPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
