package com.tis.ms.participantmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tis.ms.Application;

import common.TestContainersConfiguration;

@SpringBootApplication(scanBasePackages = {"com.tis.ms.participantmgmt"})
public class ApplicationTests {

	public static void main(String[] args) {
		System.getProperty("spring.profiles.active", "test");
		System.getProperty("seerver.port", "8091");
        SpringApplication.from(Application::main)
        .with(TestContainersConfiguration.class)
        .run(args);
    }

}
